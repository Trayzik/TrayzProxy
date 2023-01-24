package pl.trayz.proxy.server.packets.server.status.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Trayz
 **/

@Data@NoArgsConstructor@AllArgsConstructor
public class ServerStatusResponse {

    private Protocol version;
    private PlayerInfo players;
    private String description;
    private String icon;
}
