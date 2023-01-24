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
@NoArgsConstructor
@Data
public class ServerLoginDisconnectPacket extends Packet {

    private String reason;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) {
        out.writeString("{\"text\": \"" + this.reason + "\"}");
    }

    @Override
    public void read(PacketBuffer in) {
        this.reason = in.readStringFromBuffer(32767);
    }
}
