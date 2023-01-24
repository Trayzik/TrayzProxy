package pl.trayz.proxy.events;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.objects.player.Player;

/**
 * @Author: Trayz
 **/

/**
 * PlayerPermissionCheckEvent is called when a player is checking permission.
 */
@Data @RequiredArgsConstructor
public class PlayerPermissionCheckEvent {

    private final Player player;
    private final String permission;
    @Getter(AccessLevel.NONE)
    private boolean hasPermission;

    public boolean hasPermission() {
        return hasPermission;
    }
}
