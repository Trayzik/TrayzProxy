package pl.trayz.proxy.server.packets;

/**
 * @Author: Trayz
 **/

/**
 * Packet handler for server (By connection state)
 */
public interface INetHandler {

    void handlePacket(final Packet packet);


}
