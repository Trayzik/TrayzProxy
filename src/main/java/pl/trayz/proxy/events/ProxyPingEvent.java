package pl.trayz.proxy.events;

import lombok.Data;
import pl.trayz.proxy.server.packets.server.status.status.ServerStatusResponse;

import java.net.InetSocketAddress;

/**
 * @Author: Trayz
 **/

/**
 * ProxyPingEvent is called when a client is pinging the proxy.
 */
@Data
public class ProxyPingEvent {

    private ServerStatusResponse response;
    private InetSocketAddress address;

    public ProxyPingEvent(ServerStatusResponse response, InetSocketAddress address) {
        this.response = response;
        this.address = address;
    }

}
