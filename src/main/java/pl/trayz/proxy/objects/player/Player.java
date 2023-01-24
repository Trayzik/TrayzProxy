package pl.trayz.proxy.objects.player;

import pl.trayz.proxy.cache.Cache;
import pl.trayz.proxy.server.packets.server.play.*;
import pl.trayz.proxy.utils.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.PlayerServerConnectCause;
import pl.trayz.proxy.events.PlayerPermissionCheckEvent;
import pl.trayz.proxy.events.PlayerServerConnectEvent;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.chat.Message;
import pl.trayz.proxy.server.ProxyServer;
import pl.trayz.proxy.server.packets.INetHandler;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.netty.NettyCompressionCodec;
import pl.trayz.proxy.server.packets.netty.NettyPacketCodec;
import pl.trayz.proxy.server.packets.server.login.ServerLoginSetCompressionPacket;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @Author: Trayz
 **/

/**
 * Online player object
 */
@Data
public class Player {

    /**
     * Player UUID
     */
    private UUID uuid;

    /**
     * Player nickname
     */
    private String name;

    /**
     * Player game profile (Skin etc.)
     */
    private GameProfile gameProfile;

    /**
     * Server connection
     */
    private PlayerServerConnection serverConnection;

    /**
     * Player proxy connection
     */
    private Channel channel;
    private INetHandler packetHandler;
    private EnumConnectionState connectionState;

    /**
     * Proxy server instance
     */
    @Getter(AccessLevel.NONE)
    private final ProxyApp proxyApp;

    /**
     * Captcha verification
     */
    private Verification verification;

    /**
     * Player ping
     */
    private int ping;

    /**
    * player client version id
    */
    private int protocolVersion;

    /**
     * Send packet to player
     */
    public void sendPacket(final Packet packet) {
        this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Set player connection state
     */
    public void setConnectionState(final EnumConnectionState state) {
        ((NettyPacketCodec) channel.pipeline().get("packetCodec")).setConnectionState(state);
        connectionState = state;
    }

    /**
     * Set player compression
     */
    public void setCompressionThreshold(final int threshold) {
        if (!connectionState.equals(EnumConnectionState.LOGIN)) return;
        sendPacket(new ServerLoginSetCompressionPacket(threshold));
        if (channel.pipeline().get("compression") == null) {
            channel.pipeline().addBefore("packetCodec", "compression", new NettyCompressionCodec(threshold));
        } else {
            ((NettyCompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
        }
    }

    /**
     * Check if player has permission (Invoke PlayerPermissionCheckEvent)
     */
    public boolean hasPermission(final String permission) {
        PlayerPermissionCheckEvent event = new PlayerPermissionCheckEvent(this, permission);

        for(Event e : proxyApp.getRegisteredEvents()) {
            e.playerPermissionCheckEvent(event);
        }

        return event.hasPermission();
    }

    /**
     * Send default message to player
     */
    public void sendMessage(final String text) {
        sendPacket(new ServerChatPacket(text));
    }

    /**
     * Send formatted message to player
     */
    public void sendMessage(final Message message) {
        sendPacket(new ServerChatPacket(message,0));
    }

    /**
     * Send action bar message to player
     */
    public void sendActionBar(final String text) {
        sendPacket(new ServerChatPacket(text,2));
    }

    /**
     * Send title and subtitle without fade in/stay/out
     */
    public void sendTitle(final String title, final String subTitle) {
        this.sendTitle(title, subTitle, 10, 10, 10);
    }

    /**
     * Send title and subtitle with fade in/stay/out
     */
    public void sendTitle(final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        if (title != null) sendPacket(new ServerTitlePacket(0, title));
        if (subTitle != null) sendPacket(new ServerTitlePacket(1, subTitle));
        sendPacket(new ServerTitlePacket(2, fadeIn, stay, fadeOut));
    }

    /**
     * Kick player from proxy
     */
    public void kick(String reason) {
        this.disconnect(reason);
    }

    /**
     * Send disconnect packet to player
     */
    public void disconnect(String reason) {
        sendPacket(new ServerDisconnectPacket("{\"text\": \"" + reason + "\"}"));
    }

    /**
     * Check if player is connected to proxy with premium account
     */
    public boolean isOnlineMode() {
        return gameProfile != null;
    }

    /**
     * Connect player to another server
     */
    public void connect(String server) {
        PlayerServerConnectEvent event1 = new PlayerServerConnectEvent(this, server, PlayerServerConnectCause.CHANGE_SERVER,false);
        for (Event events : ProxyServer.getInstance().registeredEvents()) {
            events.playerServerConnectEvent(event1);
        }

        if(event1.isCanceled())return;

        server = event1.getTargetServer();

        String serverAddress = ProxyServer.getInstance().getServers().getOrDefault(server,null);
        if(serverAddress == null) {
            sendMessage(proxyApp.getConfigs().getMessages().getFailedConnectToServer().replace("{SERVER}", server));
            return;
        }

        String[] split = serverAddress.split(":");
        if(serverConnection != null) {
            if(serverConnection.getServer().equalsIgnoreCase(server)) {
                sendMessage(proxyApp.getConfigs().getMessages().getArleadyConnectedToServer().replace("{SERVER}", server));
                return;
            }
        }

        setServerConnection(new PlayerServerConnection(server,split[0], Integer.parseInt(split[1]), this, proxyApp,serverConnection));
    }

    /**
     * Connect player to default server
     */
    public void connectFallback() {
        PlayerServerConnectEvent event1 = new PlayerServerConnectEvent(this, proxyApp.getConfigs().getServers().getFallbackServer(), PlayerServerConnectCause.JOIN_PROXY,false);
        for (Event events : proxyApp.getRegisteredEvents()) {
            events.playerServerConnectEvent(event1);
        }

        if(event1.isCanceled()) return;

        String host = proxyApp.getConfigs().getServers().getServers().getOrDefault(event1.getTargetServer(),null);
        if(host == null) {
            disconnect(proxyApp.getConfigs().getMessages().getFailedConnectToServer().replace("{SERVER}", event1.getTargetServer()));
            return;
        }

        String[] split = host.split(":");
        setServerConnection(new PlayerServerConnection(event1.getTargetServer(),split[0], Integer.parseInt(split[1]), this, proxyApp,null));
    }

    /**
     * Connect player into void (Limbo)
     */
    public void connectToLimbo() {
        if(serverConnection != null) {
            serverConnection.close();
            setServerConnection(null);
        }

        sendPacket(new ServerJoinGamePacket(0, 0, 0, 0, 1, "default_1_1", false));
        sendPacket(new ServerPlayerPosLookPacket(new Position(0, 100, 0), 180, 90, false));
    }

    /**
     * Get player unique identifier
     */
    public UUID getUniqueId() {
        return uuid;
    }

}
