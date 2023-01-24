package pl.trayz.proxy.server.packets.server.login;

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
public class ServerLoginSetCompressionPacket extends Packet {

    private int threshold;

    {
        this.setPacketID(0x03);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.threshold);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.threshold = in.readVarIntFromBuffer();
    }
}
