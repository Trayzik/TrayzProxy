package pl.trayz.proxy.server.packets.server.status.status;

import pl.trayz.proxy.utils.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Trayz
 **/

@Data@NoArgsConstructor@AllArgsConstructor
public class PlayerInfo {

    private int onlinePlayers, maxPlayers;
    private GameProfile[] players;
}
