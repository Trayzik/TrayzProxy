package pl.trayz.proxy.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.SneakyThrows;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.cache.Cache;
import pl.trayz.proxy.configuration.configurations.Config;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.EnumPacketDirection;
import pl.trayz.proxy.events.PlayerHandshakeEvent;
import pl.trayz.proxy.events.PlayerQuitEvent;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.handlers.NetHandlerLoginServer;
import pl.trayz.proxy.server.handlers.NetHandlerStatusServer;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.handshake.HandshakePacket;
import pl.trayz.proxy.server.packets.netty.NettyPacketCodec;
import pl.trayz.proxy.server.packets.netty.NettyVarInt21FrameCodec;
import pl.trayz.proxy.server.packets.server.login.ServerLoginDisconnectPacket;
import pl.trayz.proxy.utils.Logger;
import pl.trayz.proxy.utils.ReflectionUtil;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @Author: Trayz
 **/

/**
 * Starting a proxy server with multi threads.
 */
public class MinecraftServer {

    @SneakyThrows
    public MinecraftServer(String ip, int port, ProxyApp proxyApp) {
        /**
        * Function to detect country by ip.
        */
        Config.AntiCountry antiCountry = proxyApp.getConfigs().getConfig().getAntiCountry();
        DatabaseReader databaseReader = new DatabaseReader.Builder(MinecraftServer.class.getResourceAsStream("/GeoIP.mmdb")).build();

        new ServerBootstrap()
                .group(new NioEventLoopGroup(2, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()))
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws IOException, GeoIp2Exception {

                        InetSocketAddress address = socketChannel.remoteAddress();
                        if(Cache.blocked.contains(address.getAddress().getHostAddress()) || Cache.timeBlocked.asMap().containsKey(address.getAddress().getHostAddress()) || (antiCountry.isEnabled() && !antiCountry.getAllowedIps().contains(address.getAddress().getHostAddress()) && !antiCountry.getAllowedCountries().contains(databaseReader.country(address.getAddress()).getCountry().getIsoCode()))) {
                            socketChannel.close();
                            return;
                        }

                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("timer", new ReadTimeoutHandler(10));
                        pipeline.addLast("varintCodec", new NettyVarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new NettyPacketCodec(proxyApp.getPacketsManager(), EnumConnectionState.HANDSHAKE, EnumPacketDirection.SERVERBOUND));

                        if(proxyApp.getConfigs().getConfig().isProxyProtocol()) {
                            pipeline.addFirst(new HAProxyMessageDecoder());
                        }

                        Player player = new Player(proxyApp);

                        pipeline.addLast(new SimpleChannelInboundHandler() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                /**
                                 * Detect connection
                                 * @param player.getName() - player remote host (Only until received ClientLoginStartPacket)
                                 * @param player.getChannel() - player netty channel (For sending packets etc.)
                                 */
                                player.setName(ctx.channel().remoteAddress().toString());
                                player.setChannel(ctx.channel());
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);

                                /**
                                 * Disconnecting player from server
                                 */
                                PlayerQuitEvent event = new PlayerQuitEvent(player);
                                for (Event events : proxyApp.getRegisteredEvents()) {
                                    events.playerQuitEvent(event);
                                }

                                player.getChannel().close();
                                proxyApp.getPlayers().remove(player);
                                if (player.getServerConnection() != null)
                                    player.getServerConnection().close();
                            }

                            @Override
                            public void channelRead0(ChannelHandlerContext ctx, Object msg) {
                                /*
                                 * Get real ip from proxy protocol
                                 */
                                if (msg instanceof HAProxyMessage) {
                                    HAProxyMessage proxy = (HAProxyMessage) msg;
                                    try {
                                        if (proxy.sourceAddress() != null) {
                                            Channel channel = ctx.channel();
                                            InetSocketAddress newAddress = new InetSocketAddress(proxy.sourceAddress(), proxy.sourcePort());

                                            ReflectionUtil.setFinalField(channel, ReflectionUtil.getPrivateField(channel.getClass(), "remoteAddress"), newAddress);
                                            ReflectionUtil.setFinalField(channel, ReflectionUtil.getPrivateField(channel.getClass(), "localAddress"), newAddress);

                                        }
                                    } finally {
                                        proxy.release();
                                    }
                                    return;
                                }

                                /**
                                 * Detect packet receive
                                 */

                                if (msg instanceof HandshakePacket) {
                                    final HandshakePacket handshake = (HandshakePacket) msg;

                                    switch (handshake.getNextState()) {
                                        case 1:
                                            player.setConnectionState(EnumConnectionState.STATUS);
                                            player.setPacketHandler(new NetHandlerStatusServer(player, proxyApp, handshake.getProtocolId()));
                                            break;
                                        case 2:
                                            player.setConnectionState(EnumConnectionState.LOGIN);

                                            if (handshake.getProtocolId() != 47) { // currently supported version is 1.8.8
                                                player.sendPacket(new ServerLoginDisconnectPacket(proxyApp.getConfigs().getMessages().getIncorrectMinecraftVersion()));
                                                return;
                                            }

                                            if (proxyApp.getConfigs().getConfig().getMaxPlayers() != -1 && proxyApp.getPlayers().size() >= proxyApp.getConfigs().getConfig().getMaxPlayers()) {
                                                player.sendPacket(new ServerLoginDisconnectPacket(proxyApp.getConfigs().getMessages().getServerIsFull()));
                                                return;
                                            }

                                            if (Cache.lastConnections.asMap().containsKey(address.getAddress().getHostAddress())) {
                                                Cache.timeBlocked.put(address.getAddress().getHostAddress(), (byte) 0);
                                                socketChannel.close();
                                                return;
                                            }

                                            if (proxyApp.getConfigs().getConfig().isConnectionLimitForIP())
                                                Cache.lastConnections.put(address.getAddress().getHostAddress(), (byte) 0);

                                            PlayerHandshakeEvent event = new PlayerHandshakeEvent(handshake, ctx.channel());
                                            for (Event events : proxyApp.getRegisteredEvents()) {
                                                events.playerHandshakeEvent(event);
                                            }

                                            player.setProtocolVersion(handshake.getProtocolId());
                                            player.setPacketHandler(new NetHandlerLoginServer(player, proxyApp));
                                            break;
                                    }
                                    if (player.getConnectionState().equals(EnumConnectionState.HANDSHAKE)) {
                                        player.getChannel().close();
                                    }
                                } else {
                                    if (player.getPacketHandler() != null)
                                        player.getPacketHandler().handlePacket((Packet) msg);
                                }
                            }

                        });
                    }
                }).bind(port).addListener((ChannelFutureListener) channelFuture -> Logger.logInfo("Started server on " + ip + ":" + port));
    }
}
