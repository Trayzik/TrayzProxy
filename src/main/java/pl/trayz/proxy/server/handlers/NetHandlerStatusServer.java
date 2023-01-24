package pl.trayz.proxy.server.handlers;

import lombok.SneakyThrows;
import pl.trayz.proxy.utils.ImageUtil;
import pl.trayz.proxy.utils.authlib.GameProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.events.ProxyPingEvent;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.packets.INetHandler;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.client.status.ClientStatusPingPacket;
import pl.trayz.proxy.server.packets.client.status.ClientStatusRequestPacket;
import pl.trayz.proxy.server.packets.server.status.ServerStatusPongPacket;
import pl.trayz.proxy.server.packets.server.status.ServerStatusResponsePacket;
import pl.trayz.proxy.server.packets.server.status.status.PlayerInfo;
import pl.trayz.proxy.server.packets.server.status.status.Protocol;
import pl.trayz.proxy.server.packets.server.status.status.ServerStatusResponse;
import pl.trayz.proxy.utils.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * @Author: Trayz
 **/

/**
 * Handler for status packets.
 */
@RequiredArgsConstructor
@Data
public class NetHandlerStatusServer implements INetHandler {

    /**
     * Proxy player.
     */
    private final Player player;

    /**
     * Proxy instance.
     */
    private final ProxyApp proxyApp;

    /**
     * Protocol version.
     */
    private final int protocol;

    @SneakyThrows
    @Override
    public void handlePacket(Packet packet) {
        if(packet instanceof ClientStatusRequestPacket) {
            /**
             * Handle status request packet.
             * Send status response packet with server info.
             */
            if (proxyApp.getConfigs().getConfig().isLogPings()) {
                InetSocketAddress address = (InetSocketAddress) player.getChannel().remoteAddress();
                Logger.logInfo("Detected ping from " + address.getAddress().getHostAddress() + ":" + address.getPort());
            }
            ServerStatusResponse responsePacket = new ServerStatusResponse();
            responsePacket.setDescription(proxyApp.getConfigs().getConfig().getMotd());
            PlayerInfo players = new PlayerInfo();
            players.setMaxPlayers(proxyApp.getConfigs().getConfig().getMaxPlayers());
            players.setOnlinePlayers(proxyApp.getPlayers().size());
            players.setPlayers(new GameProfile[0]);
            responsePacket.setPlayers(players);

            final File statusFile = new File("server-icon.png");
            BufferedImage bufferedImage = null;
            if(statusFile.exists()) {
                bufferedImage = ImageIO.read(new File("server-icon.png"));
            }

            responsePacket.setIcon(statusFile.exists() ? ImageUtil.iconToString(bufferedImage) : null);
            responsePacket.setVersion(new Protocol("1.8.8x", Math.max(protocol, 47)));

            ProxyPingEvent event = new ProxyPingEvent(responsePacket, (InetSocketAddress)player.getChannel().remoteAddress());
            for (Event events : proxyApp.getRegisteredEvents()) {
                events.proxyPingEvent(event);
            }

            player.sendPacket(new ServerStatusResponsePacket(event.getResponse()));
        }else if (packet instanceof ClientStatusPingPacket) {
            /**
             * Request packet is sent by the client to request the server's status.
             */
            player.sendPacket(new ServerStatusPongPacket(((ClientStatusPingPacket) packet).getTime()));
            player.getChannel().close();
        }
    }

}
