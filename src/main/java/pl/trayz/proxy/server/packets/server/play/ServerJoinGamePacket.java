package pl.trayz.proxy.server.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerJoinGamePacket extends Packet {

    private int entityId;
    public int gamemode;
    public int dimension;
    public int difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reduced_debug;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.entityId);
        out.writeByte(this.gamemode);
        out.writeByte(this.dimension);
        out.writeByte(this.difficulty);
        out.writeByte(this.maxPlayers);
        out.writeString(this.levelType);
        out.writeBoolean(this.reduced_debug);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readInt();
        this.gamemode = in.readUnsignedByte();
        this.dimension = in.readByte();
        this.difficulty = in.readUnsignedByte();
        this.maxPlayers = in.readUnsignedByte();
        this.levelType = in.readStringFromBuffer(32767);
        this.reduced_debug = in.readBoolean();
    }
}
