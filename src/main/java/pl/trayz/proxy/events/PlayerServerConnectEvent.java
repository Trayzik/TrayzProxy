package pl.trayz.proxy.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.enums.PlayerServerConnectCause;
import pl.trayz.proxy.objects.player.Player;

/**
 * @Author: Trayz
 **/

/**
 * PlayerServerConnectEvent is called when a player is connecting to a server.
 */
@Data @AllArgsConstructor
public class PlayerServerConnectEvent {

    private final Player player;
    private String targetServer;
    private final PlayerServerConnectCause cause;
    private boolean canceled;

}
