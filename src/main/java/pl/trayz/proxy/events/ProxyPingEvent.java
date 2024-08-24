package pl.trayz.proxy.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.server.packets.server.status.status.ServerStatusResponse;

import java.net.InetSocketAddress;

/**
 * @Author: Trayz
 **/

/**
 * ProxyPingEvent is called when a client is pinging the proxy.
 */
@Data @AllArgsConstructor
public class ProxyPingEvent {

    private ServerStatusResponse response;
    private InetSocketAddress address;

}
