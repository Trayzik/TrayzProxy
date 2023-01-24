package pl.trayz.proxy.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.objects.player.Player;

/**
 * @Author: Trayz
 **/

/**
 * PlayerQuitEvent is called when a player disconnect from the proxy.
 */
@Data @AllArgsConstructor
public class PlayerQuitEvent {

    private Player player;

}
