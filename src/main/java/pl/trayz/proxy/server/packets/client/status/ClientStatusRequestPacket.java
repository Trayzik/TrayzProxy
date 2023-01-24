package pl.trayz.proxy.server.packets.client.status;

import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/
public class ClientStatusRequestPacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
    }
}
