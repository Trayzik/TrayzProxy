package pl.trayz.proxy.server.packets.client.play;

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
public class ClientPlayerPositionPacket extends Packet {

    private double x;
    private double y;
    private double z;
    private boolean onGround;


    {
        this.setPacketID(0x04);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeByte(this.onGround ? 1 : 0);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.onGround = in.readUnsignedByte() != 0;
    }
}