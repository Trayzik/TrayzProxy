package pl.trayz.proxy.server.packets.server.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

import java.util.UUID;

/**
 * @Author: Trayz
 **/

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServerLoginSuccessPacket extends Packet {

    private UUID uuid;
    private String username;

    {
        this.setPacketID(0x02);
    }

    @Override
    public void write(PacketBuffer out) {
        out.writeString(this.uuid.toString());
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in) {
        this.uuid = UUID.fromString(in.readStringFromBuffer(86));
        this.username = in.readStringFromBuffer(32);
    }
}
