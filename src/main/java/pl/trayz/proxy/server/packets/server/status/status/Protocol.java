package pl.trayz.proxy.server.packets.server.status.status;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: Trayz
 **/

@Data @AllArgsConstructor
public class Protocol {

    private String name;
    private int protocol;

}
