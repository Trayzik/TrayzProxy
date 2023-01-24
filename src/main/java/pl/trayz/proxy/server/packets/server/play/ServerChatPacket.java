package pl.trayz.proxy.server.packets.server.play;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.objects.chat.Message;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@NoArgsConstructor
@Data
public class ServerChatPacket extends Packet {

    private Message message;
    private int position;

    {
        this.setPacketID(0x02);
    }

    public ServerChatPacket(String message) {
        this(message, 0);
    }

    public ServerChatPacket(String message, int position) {
        this(Message.fromString(message), position);
    }

    public ServerChatPacket(Message message,int position) {
        this.message = message;
        this.position = position;
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(message.toJsonString());
        out.writeByte(position);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.message = Message.fromString(in.readStringFromBuffer(32767));
        this.position = in.readByte();
    }
}