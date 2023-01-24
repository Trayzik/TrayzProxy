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
public class ServerEncryptionRequestPacket extends Packet {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.serverId = in.readStringFromBuffer(20);
        this.publicKey = in.readByteArray();
        this.verifyToken = in.readByteArray();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.serverId);
        out.writeByteArray(this.publicKey);
        out.writeByteArray(this.verifyToken);
    }

}
