package pl.trayz.proxy.objects.player;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.FutureListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.EnumPacketDirection;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.client.play.ClientKeepAlivePacket;
import pl.trayz.proxy.server.packets.client.login.ClientLoginStartPacket;
import pl.trayz.proxy.server.packets.handshake.HandshakePacket;
import pl.trayz.proxy.server.packets.netty.NettyCompressionCodec;
import pl.trayz.proxy.server.packets.netty.NettyPacketCodec;
import pl.trayz.proxy.server.packets.netty.NettyVarInt21FrameCodec;
import pl.trayz.proxy.server.packets.server.login.ServerEncryptionRequestPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginDisconnectPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginSetCompressionPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginSuccessPacket;
import pl.trayz.proxy.server.packets.server.play.*;
import pl.trayz.proxy.utils.AddressUtil;
import pl.trayz.proxy.utils.Dispatcher;

import java.net.InetSocketAddress;

/**
 * @Author: Trayz
 **/

/**
 * Connection to the server
 */
@Data @AllArgsConstructor
public class PlayerServerConnection {

    private Channel channel;
    public boolean connected;
    private final Player player;
    private EnumConnectionState connectionState = EnumConnectionState.LOGIN;
    private final String server;

    public PlayerServerConnection(String server,String hostname, int port, Player player, ProxyApp proxyApp,PlayerServerConnection lastConnection) {
        this.server = server;
        this.player = player;
        player.setServerConnection(this);
        Dispatcher.getService().submit(() -> {
            final Bootstrap bootstrap = new Bootstrap()
                    .group(new NioEventLoopGroup(2, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()))
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            final ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("timer", new ReadTimeoutHandler(20));
                            pipeline.addLast("frameCodec", new NettyVarInt21FrameCodec());
                            pipeline.addLast("packetCodec", new NettyPacketCodec(proxyApp.getPacketsManager(), EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND));
                            pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    HandshakePacket handshakePacket = new HandshakePacket(47, hostname, port, 2);
                                    if (proxyApp.getConfigs().getConfig().isIpForwarding()) {
                                        InetSocketAddress address = (InetSocketAddress) player.getChannel().remoteAddress();
                                        String newHost = hostname + "\00" + AddressUtil.sanitizeAddress(address) + "\00" + player.getUuid();
                                        if (player.getGameProfile() != null) {
                                            newHost = newHost + "\00" + new Gson().toJson(player.getGameProfile().getProperties().values().toArray());
                                        }
                                        handshakePacket.setHost(newHost);
                                    }
                                    sendPacket(handshakePacket);
                                    sendPacket(new ClientLoginStartPacket(player.getName()));

                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) {
                                    if (!connected) return;
                                    player.kick("§cServer connection lost");
                                    player.setServerConnection(null);
                                    close();
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                    /**
                                     * On packet receive from server
                                     */
                                    if (packet instanceof ServerLoginSetCompressionPacket) {
                                        setCompressionThreshold(((ServerLoginSetCompressionPacket) packet).getThreshold());
                                    } else if (packet instanceof ServerEncryptionRequestPacket) {
                                        player.kick(proxyApp.getConfigs().getMessages().getMojangAuthenticationFailed());
                                    } else if (packet instanceof ServerLoginSuccessPacket) {
                                        setConnectionState(EnumConnectionState.PLAY);
                                    } else if (packet instanceof ServerDisconnectPacket) {
                                        player.sendPacket(new ServerDisconnectPacket(((ServerDisconnectPacket) packet).getReason()));
                                    } else if (packet instanceof ServerLoginDisconnectPacket) {
                                        player.sendPacket(new ServerDisconnectPacket(((ServerLoginDisconnectPacket) packet).getReason()));
                                    } else if (packet instanceof ServerJoinGamePacket) {
                                        connected = true;
                                        ServerJoinGamePacket serverJoinGamePacket = (ServerJoinGamePacket) packet;
                                        player.sendPacket(serverJoinGamePacket);
                                        player.sendPacket(new ServerRespawnPacket(serverJoinGamePacket.getDimension(), serverJoinGamePacket.getDifficulty(), serverJoinGamePacket.getGamemode(), serverJoinGamePacket.getLevelType()));
                                    } else if (packet instanceof ServerKeepAlivePacket) {
                                        sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) packet).getKeepaliveId()));
                                    } else if (connected && connectionState == EnumConnectionState.PLAY) {
                                        player.sendPacket(packet);
                                    }
                                }
                            });
                        }
                    });
            final ChannelFuture channelFuture = bootstrap.connect(hostname, port);
            this.channel = channelFuture.channel();
            this.channel.config().setOption(ChannelOption.TCP_NODELAY, true);
            this.channel.config().setOption(ChannelOption.IP_TOS, 0x18);

            channelFuture.addListener((FutureListener<Void>) future -> {
                if (!channelFuture.isSuccess()) {
                    player.sendMessage(proxyApp.getConfigs().getMessages().getFailedConnectToServer().replace("{SERVER}", server));
                    if (lastConnection != null) {
                        player.setServerConnection(lastConnection);
                    } else {
                        player.kick("§cFailed to connect to server");
                    }
                } else {
                    if (lastConnection != null) {
                        lastConnection.setConnected(false);
                        lastConnection.close();
                    }
                }
            });
        });
    }

    /**
     * Set connection state on server
     */
    public void setConnectionState(final EnumConnectionState state) {
        ((NettyPacketCodec) channel.pipeline().get("packetCodec")).setConnectionState(state);
        connectionState = state;
    }

    /**
     * Close connection with server
     */
    public void close() {
        connected = false;
        this.channel.close();
    }

    /**
     * Send packet from player to target server
     */
    public void sendPacket(final Packet packet) {
        this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Set compression threshold with server
     */
    public void setCompressionThreshold(final int threshold) {
        if (connectionState == EnumConnectionState.LOGIN) {
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new NettyCompressionCodec(threshold));
            } else {
                ((NettyCompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
