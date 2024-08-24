package pl.trayz.proxy.server.handlers;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pl.trayz.proxy.utils.Dispatcher;
import pl.trayz.proxy.utils.authlib.GameProfile;
import pl.trayz.proxy.utils.authlib.Property;
import pl.trayz.proxy.utils.authlib.UUIDTypeAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.events.PlayerJoinEvent;
import pl.trayz.proxy.events.PlayerPreLoginEvent;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.objects.player.Verification;
import pl.trayz.proxy.server.packets.INetHandler;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.client.login.ClientEncryptionResponsePacket;
import pl.trayz.proxy.server.packets.client.login.ClientLoginStartPacket;
import pl.trayz.proxy.server.packets.netty.cipher.NettyEncryptingDecoder;
import pl.trayz.proxy.server.packets.netty.cipher.NettyEncryptingEncoder;
import pl.trayz.proxy.server.packets.server.login.ServerEncryptionRequestPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginDisconnectPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginSuccessPacket;
import pl.trayz.proxy.server.packets.server.play.*;
import pl.trayz.proxy.utils.CryptUtil;
import pl.trayz.proxy.utils.Logger;
import pl.trayz.proxy.objects.player.Position;

import javax.crypto.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: Trayz
 **/

/**
 * Handler for login packets.
 */
@RequiredArgsConstructor
@Data
public class NetHandlerLoginServer implements INetHandler {

    /**
     * Proxy player.
     */
    private final Player player;

    /**
     * Proxy instance.
     */
    private final ProxyApp proxyApp;

    /**
     * Generated key for minecraft authentication.
     */
    private KeyPair keyPair;

    /**
     * Generated server id for minecraft authentication.
     */
    private String serverId;

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof ClientLoginStartPacket) {
            /**
             * First packet sent by client after handshake.
             * Contains player name.
             */
            player.setName(((ClientLoginStartPacket) packet).getUsername());

            /**
             * Verify player name for bad words and length.
             */
            if (player.getName().length() > 16 || player.getName().length() < 3 || !player.getName().matches("^[a-zA-Z0-9_]*$")) {
                player.sendPacket(new ServerLoginDisconnectPacket(proxyApp.getConfigs().getMessages().getInvalidNickname()));
                return;
            }

            /**
             * Checking if player is already connected to proxy.
             */
            if (proxyApp.getPlayers().stream().anyMatch(p -> p.getName().equals(player.getName()))) {
                player.sendPacket(new ServerLoginDisconnectPacket(proxyApp.getConfigs().getMessages().getYouAreArleadyConnectedToProxy()));
                return;
            }

            /**
             * Log connection
             */
            if (proxyApp.getConfigs().getConfig().isLogConnections()) {
                InetSocketAddress address = (InetSocketAddress) player.getChannel().remoteAddress();
                Logger.logInfo("Detected connection from " + player.getName() + " [" + address.getAddress().getHostAddress() + ":" + address.getPort() + "]");
            }

            /**
             * Invoke player pre login event.
             */
            PlayerPreLoginEvent event = new PlayerPreLoginEvent(player.getName(), player.getChannel(), proxyApp.getConfigs().getConfig().isOnlineMode());
            for (Event events : proxyApp.getRegisteredEvents()) {
                events.playerPreLoginEvent(event);
            }

            if (event.isCancelled()) {
                player.sendPacket(new ServerLoginDisconnectPacket(event.getCancelReason()));
                return;
            }

            /**
             * Check if player is in online mode.
             * If not, finish login.
             * If yes, generate key pair and server id and send encryption packet.
             */
            if (!event.isOnlineMode()) {
                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(Charsets.UTF_8));
                player.setUuid(uuid);
                finish(false);
            } else {
                keyPair = CryptUtil.generateKeyPair();
                Random random = new Random();
                serverId = Long.toString(random.nextLong(), 16);
                final byte[] pubKey = keyPair.getPublic().getEncoded();
                final byte[] verify = new byte[4];
                random.nextBytes(verify);
                player.sendPacket(new ServerEncryptionRequestPacket(serverId, pubKey, verify));
            }

        } else if (packet instanceof ClientEncryptionResponsePacket) {
            /**
             * Checking if player is online mode using minecraft authentication.
             * Using multiple threads to prevent blocking main thread.
             */
            Dispatcher.getService().submit(() -> {
                try {
                    ClientEncryptionResponsePacket response = (ClientEncryptionResponsePacket) packet;

                    final SecretKey sharedKey = CryptUtil.decryptSharedKey(keyPair.getPrivate(), response.getSharedSecret());
                    final MessageDigest sha = MessageDigest.getInstance("SHA-1");

                    ChannelPipeline pipeline = player.getChannel().pipeline();
                    pipeline.addFirst("decrypt", new NettyEncryptingDecoder(CryptUtil.createNetCipherInstance(2, sharedKey)));
                    pipeline.addFirst("encrypt", new NettyEncryptingEncoder(CryptUtil.createNetCipherInstance(1, sharedKey)));

                    for (final byte[] bit : new byte[][]{serverId.getBytes(StandardCharsets.UTF_8), sharedKey.getEncoded(), keyPair.getPublic().getEncoded()}) {
                        sha.update(bit);
                    }

                    final String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), StandardCharsets.UTF_8);

                    final String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + player.getName() + "&serverId=" + encodedHash;
                    HttpURLConnection connection = (HttpURLConnection) new URL(authURL).openConnection();

                    JsonParser parser = new JsonParser();
                    JsonElement jsonElement = parser.parse(new InputStreamReader(connection.getInputStream()));

                    if (jsonElement.isJsonNull()) {
                        player.sendPacket(new ServerLoginDisconnectPacket(proxyApp.getConfigs().getMessages().getMojangAuthenticationFailed()));
                        return;
                    }

                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonObject properties = jsonObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();

                    player.setUuid(UUIDTypeAdapter.fromString(jsonObject.get("id").getAsString()));

                    GameProfile gameProfile = new GameProfile(player.getUuid(), player.getName());
                    gameProfile.getProperties().put("textures", new Property("textures", properties.get("value").getAsString(), properties.get("signature").getAsString()));
                    player.setGameProfile(gameProfile);

                    finish(true);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    player.sendPacket(new ServerLoginDisconnectPacket(proxyApp.getConfigs().getMessages().getMojangAuthenticationFailed()));
                }
            });
        }
    }

    /**
     * Finish login process.
     *
     * @param premium is player online mode.
     */
    @SneakyThrows
    private void finish(boolean premium) {
        player.setCompressionThreshold(256);
        player.sendPacket(new ServerLoginSuccessPacket(player.getUuid(), player.getName()));
        player.setConnectionState(EnumConnectionState.PLAY);
        player.setPacketHandler(new NetHandlerPlayServer(player, proxyApp));
        proxyApp.getPlayers().add(player);

        /**
         * Checking if player need to be verified by captcha and anti-bot.
         * If player need to be verified, then send him to limbo server.
         */
        if((premium && proxyApp.getConfigs().getConfig().isCaptchaForPremium()) || (!premium && proxyApp.getConfigs().getConfig().isCaptchaForNonPremium())) {
            player.connectToLimbo();

            player.sendTitle("§2§lCAPTCHA", proxyApp.getConfigs().getMessages().getPleaseEnterCaptcha(), 0, 11*20, 0);

            Random random = new Random();
            int first = random.nextInt(10);
            int second = random.nextInt(10);
            boolean addition = random.nextBoolean();
            int result = addition ? first + second : first - second;

            player.sendMessage(proxyApp.getConfigs().getMessages().getCaptcha().replace("{QUESTION}",first+(addition ? " + " : " - ")+second));

            player.setVerification(new Verification(String.valueOf(result)));

            return;
        }

        PlayerJoinEvent event = new PlayerJoinEvent(player);
        for (Event events : proxyApp.getRegisteredEvents()) {
            events.playerJoinEvent(event);
        }

        /**
         * Connecting player into fallback server.
         */
        player.connectFallback();
    }


}
