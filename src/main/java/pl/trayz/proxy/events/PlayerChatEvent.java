package pl.trayz.proxy.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.objects.player.Player;

/**
 * @Author: Trayz
 **/

/**
 * PlayerChatEvent is called when a player send a message on chat.
 */
@Data@AllArgsConstructor
public class PlayerChatEvent {

    private final Player player;
    private String message;
    private boolean canceled;
}
