package pl.trayz.proxy.server.packets.server.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.trayz.proxy.objects.chat.Message;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;

/**
 * @Author: Trayz
 **/

@NoArgsConstructor
@Getter
public class ServerTitlePacket extends Packet {

    private int titleAction;
    private Message title;
    private Message subTitle;
    private int fadeIn;
    private int fadeOut;
    private int stay;

    {
        this.setPacketID(0x45);
    }

    public ServerTitlePacket(int action, String message) {
        this.titleAction = action;
        if (action == 0) {
            this.title = Message.fromString(message);
        } else if (action == 1) {
            this.subTitle = Message.fromString(message);
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket!");
        }
    }

    public ServerTitlePacket(int action, int fadeIn, int stay, int fadeOut) {
        this.titleAction = action;
        if (titleAction == 2) {
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket");
        }
    }


    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(titleAction);
        switch (titleAction) {
            case 0:
                out.writeString(this.title.toJsonString());
                break;
            case 1:
                out.writeString(this.subTitle.toJsonString());
                break;
            case 2:
                out.writeInt(this.fadeIn);
                out.writeInt(this.stay);
                out.writeInt(this.fadeOut);
                break;
            default:
                break;
        }
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.titleAction = in.readVarIntFromBuffer();
        switch (titleAction) {
            case 0:
                this.title = Message.fromString(in.readStringFromBuffer(32767));
                break;
            case 1:
                this.subTitle = Message.fromString(in.readStringFromBuffer(32767));
                break;
            case 2:
                this.fadeIn = in.readInt();
                this.stay = in.readInt();
                this.fadeOut = in.readInt();
                break;
            default:
                break;
        }
    }
}