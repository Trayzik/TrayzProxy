package pl.trayz.proxy.server.packets.handshake;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;
import pl.trayz.proxy.utils.Logger;

/**
 * @Author: Trayz
 **/

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HandshakePacket extends Packet {

    private int protocolId;
    private String host;
    private int port;
    private int nextState;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) {
        out.writeVarIntToBuffer(this.protocolId);
        out.writeString(this.host);
        out.writeShort(this.port);
        out.writeVarIntToBuffer(this.nextState);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        try {
            this.protocolId = in.readVarIntFromBuffer();
            this.host = in.readStringFromBuffer(255);
            this.port = in.readShort();
            this.nextState = in.readVarIntFromBuffer();

            if (this.host.contains("\0")) {
                String[] split = this.host.split( "\0", 2 );
                this.host = split[0];
            }

            if (this.host.endsWith(".")) {
                this.host = this.host.substring( 0, this.host.length() - 1 );
            }
        }catch (Exception ignored) {
        }
    }
}
