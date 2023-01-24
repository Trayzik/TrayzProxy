package pl.trayz.proxy.server.packets.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import java.util.List;

/**
 * @Author: Trayz
 **/

/**
 * Using for decrypting packets when player is premium.
 */
public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final NettyEncryptionTranslator decryptionCodec;

    public NettyEncryptingDecoder(Cipher cipher) {
        this.decryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf object, List list) throws Exception {
        list.add(this.decryptionCodec.decipher(channelHandlerContext, object));
    }
}