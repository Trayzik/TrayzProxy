package pl.trayz.proxy.events;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.server.packets.handshake.HandshakePacket;

/**
 * @Author: Trayz
 **/

/**
 * PlayerHandshakeEvent is called when a player is sending a handshake (Only login, this event is called before PlayerPreLoginEvent).
 */
@Data@AllArgsConstructor
public class PlayerHandshakeEvent {

    private final HandshakePacket handshakePacket;
    private final Channel channel;

}
