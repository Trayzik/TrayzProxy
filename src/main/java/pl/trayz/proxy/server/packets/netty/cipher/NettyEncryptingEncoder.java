package pl.trayz.proxy.server.packets.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;
import java.util.List;

/**
 * @Author: Trayz
 **/

/**
 * Using for crypting packets when player is premium.
 */
public class NettyEncryptingEncoder extends MessageToByteEncoder<ByteBuf> {

    private final NettyEncryptionTranslator encryptionCodec;

    public NettyEncryptingEncoder(Cipher cipher) {
        this.encryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        this.encryptionCodec.cipher(byteBuf, byteBuf2);
    }
}
