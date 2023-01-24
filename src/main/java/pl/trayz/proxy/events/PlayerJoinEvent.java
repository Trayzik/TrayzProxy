package pl.trayz.proxy.events;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.objects.player.Player;

/**
 * @Author: Trayz
 **/

/**
 * PlayerJoinEvent is called when a player join the proxy (after PlayerPreLoginEvent).
 */
@Data @RequiredArgsConstructor
public class PlayerJoinEvent {

    private final Player player;

}
