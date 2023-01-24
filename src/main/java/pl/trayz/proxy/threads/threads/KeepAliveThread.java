package pl.trayz.proxy.threads.threads;

import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.server.packets.server.play.ServerKeepAlivePacket;

/**
 * @Author: Trayz
 **/

/**
 * Sending keep alive packets
 * (Used to keep connection alive with minecraft server)
 * Checking verifications
 */
@RequiredArgsConstructor
public class KeepAliveThread implements Runnable{

    private final ProxyApp proxyApp;

    @Override
    public void run() {
        proxyApp.getPlayers().forEach(player -> {
            if(!player.getConnectionState().equals(EnumConnectionState.PLAY)) return;
            player.sendPacket(new ServerKeepAlivePacket((int)System.currentTimeMillis()));

            if(player.getVerification() != null && (System.currentTimeMillis() - player.getVerification().getTime()) > 10000)
                player.disconnect(proxyApp.getConfigs().getMessages().getFailedToVerificateCaptcha());
        });
    }
}
