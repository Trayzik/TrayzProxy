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
public class ClientLoginStartPacket extends Packet {

    private String username;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) {
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in){
        try {
            this.username = in.readStringFromBuffer(16);
        }catch (Exception e){
            this.username = "";
        }
    }
}
