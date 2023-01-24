package pl.trayz.proxy.server.packets.client.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@RequiredArgsConstructor
@Data
@AllArgsConstructor
public class ClientEncryptionResponsePacket extends Packet {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.sharedSecret = in.readByteArray(128);
        this.verifyToken = in.readByteArray(128);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByteArray(this.sharedSecret);
        out.writeByteArray(this.verifyToken);
    }
}
