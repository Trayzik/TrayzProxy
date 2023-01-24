package pl.trayz.proxy.server.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerKeepAlivePacket extends Packet {

    private int keepaliveId;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out){
        out.writeVarIntToBuffer(this.keepaliveId);
    }

    @Override
    public void read(PacketBuffer in){
        this.keepaliveId = in.readVarIntFromBuffer();
    }
}
