package pl.trayz.proxy.server.packets.server.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ServerStatusPongPacket extends Packet {

    private long time;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeLong(this.time);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.time = in.readLong();
    }
}
