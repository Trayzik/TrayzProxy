package pl.trayz.proxy.server.packets.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ClientKeepAlivePacket extends Packet {

    private int time;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.time);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.time = in.readVarIntFromBuffer();
    }
}
