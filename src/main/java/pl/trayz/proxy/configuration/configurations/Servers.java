package pl.trayz.proxy.configuration.configurations;

import com.google.common.collect.Maps;
import lombok.Data;
import pl.trayz.proxy.configuration.api.annotations.Config;

import java.util.Map;

/**
 * @Author: Trayz
 **/

/**
 * Default proxy config for servers to connect
 */
@Config(
        file = "servers.json"
)
@Data
public class Servers {

    private String fallbackServer;
    private Map<String,String> servers;

    public Servers() {
        this.fallbackServer = "lobby";
        this.servers = Maps.newConcurrentMap();
        this.servers.put("lobby","0.0.0.0:25566");
    }
}
