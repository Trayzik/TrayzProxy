package pl.trayz.proxy.server.packets.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.EnumPacketDirection;
import pl.trayz.proxy.server.packets.CustomPacket;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;
import pl.trayz.proxy.server.packets.PacketsManager;

import java.util.List;

/**
 * @Author: Trayz
 **/

@AllArgsConstructor
@Data
public class NettyPacketCodec extends ByteToMessageCodec<Packet> {

    private final PacketsManager packetsManager;
    private EnumConnectionState connectionState;
    private final EnumPacketDirection packetDirection;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        try {
            final PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
            if (packet.isCustom()) {
                packetbuffer.writeVarIntToBuffer(packet.getPacketID());
                packetbuffer.writeBytes(packet.getCustomData());
            } else {
                packetbuffer.writeVarIntToBuffer(packet.getPacketID());
                packet.write(packetbuffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) {
        if (!byteBuf.isReadable()) return;
        try {
            final PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

            final int packetId = packetBuffer.readVarIntFromBuffer();

            Packet packet = packetsManager.getPacket(connectionState, packetDirection, packetId);

            if (packet == null) {
                packet = new CustomPacket();
                final byte[] data = new byte[packetBuffer.readableBytes()];
                packetBuffer.readBytes(data);
                packet.setCustom(packetId, data);
            } else {
                packet.read(packetBuffer);
            }

            if (packetBuffer.isReadable()) {
                throw new DecoderException(String.format("Packet (%s) was larger than i expected found %s bytes extra", packet.getClass().getSimpleName(), packetBuffer.readableBytes()));
            }
            list.add(packet);
            byteBuf.clear();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
