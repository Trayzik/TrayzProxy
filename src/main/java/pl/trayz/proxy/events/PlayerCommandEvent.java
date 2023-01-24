package pl.trayz.proxy.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.objects.player.Player;

/**
 * @Author: Trayz
 **/

/**
 * PlayerCommandEvent is called when a player is executing a command.
 */
@Data
@AllArgsConstructor
public class PlayerCommandEvent {

    private final Player player;
    private final String command;
    private boolean canceled;
}
