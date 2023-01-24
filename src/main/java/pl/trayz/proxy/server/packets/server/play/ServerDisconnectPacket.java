package pl.trayz.proxy.server.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerDisconnectPacket extends Packet {

    private String reason;

    {
        this.setPacketID(0x40);
    }


    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.reason);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.reason = in.readStringFromBuffer(32767);
    }
}
