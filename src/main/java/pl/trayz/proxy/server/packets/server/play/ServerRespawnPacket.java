package pl.trayz.proxy.server.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerRespawnPacket extends Packet {

    public int dimension;
    public int difficulty;
    public int gamemode;
    private String level_type;

    {
        this.setPacketID(0x07);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.dimension);
        out.writeByte(this.difficulty);
        out.writeByte(this.gamemode);
        out.writeString(this.level_type);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.dimension = in.readInt();
        this.difficulty = in.readUnsignedByte();
        this.gamemode = in.readUnsignedByte();
        this.level_type = in.readStringFromBuffer(24);
    }
}
