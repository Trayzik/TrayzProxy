package pl.trayz.proxy.server.packets;

import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.EnumPacketDirection;
import pl.trayz.proxy.server.packets.client.play.ClientKeepAlivePacket;
import pl.trayz.proxy.server.packets.client.login.ClientEncryptionResponsePacket;
import pl.trayz.proxy.server.packets.client.login.ClientLoginStartPacket;
import pl.trayz.proxy.server.packets.client.play.ClientChatPacket;
import pl.trayz.proxy.server.packets.client.play.ClientPlayerPositionPacket;
import pl.trayz.proxy.server.packets.client.status.ClientStatusPingPacket;
import pl.trayz.proxy.server.packets.client.status.ClientStatusRequestPacket;
import pl.trayz.proxy.server.packets.handshake.HandshakePacket;
import pl.trayz.proxy.server.packets.server.login.ServerEncryptionRequestPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginDisconnectPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginSetCompressionPacket;
import pl.trayz.proxy.server.packets.server.login.ServerLoginSuccessPacket;
import pl.trayz.proxy.server.packets.server.play.*;
import pl.trayz.proxy.server.packets.server.status.ServerStatusPongPacket;
import pl.trayz.proxy.server.packets.server.status.ServerStatusResponsePacket;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * @Author: Trayz
 **/

/**
 * Registering minecraft packets
 */
public class PacketsManager {

    private static HashMap<Integer, Packet> CLIENT_STATUS = new HashMap<>();
    private static HashMap<Integer, Packet> CLIENT_LOGIN = new HashMap<>();
    private static HashMap<Integer, Packet> CLIENT_PLAY = new HashMap<>();

    private static HashMap<Integer, Packet> SERVER_STATUS = new HashMap<>();
    private static HashMap<Integer, Packet> SERVER_LOGIN = new HashMap<>();
    private static HashMap<Integer, Packet> SERVER_PLAY = new HashMap<>();

    public PacketsManager() {
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.SERVERBOUND, new ClientLoginStartPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginDisconnectPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.SERVERBOUND, new ClientEncryptionResponsePacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerEncryptionRequestPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginSetCompressionPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginSuccessPacket());

        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerJoinGamePacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerKeepAlivePacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerRespawnPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerDisconnectPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerPlayerPosLookPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerChatPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerTitlePacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientChatPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientKeepAlivePacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientPlayerPositionPacket());

        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.CLIENTBOUND, new ServerStatusPongPacket());
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.CLIENTBOUND, new ServerStatusResponsePacket());
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.SERVERBOUND, new ClientStatusRequestPacket());
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.SERVERBOUND, new ClientStatusPingPacket());
    }

    public Packet getPacket(EnumConnectionState connectionState, EnumPacketDirection direction, int id) {
        return getNewInstance(getPacketA(connectionState, direction, id));
    }

    private Packet getPacketA(EnumConnectionState connectionState, EnumPacketDirection direction, int id) {
        switch (direction) {
            case SERVERBOUND:
                switch (connectionState) {
                    case HANDSHAKE:
                        return new HandshakePacket();
                    case LOGIN:
                        return CLIENT_LOGIN.get(id);
                    case PLAY:
                        return CLIENT_PLAY.get(id);
                    case STATUS:
                        return CLIENT_STATUS.get(id);
                }
                break;
            case CLIENTBOUND:
                switch (connectionState) {
                    case LOGIN:
                        return SERVER_LOGIN.get(id);
                    case PLAY:
                        return SERVER_PLAY.get(id);
                    case STATUS:
                        return SERVER_STATUS.get(id);

                }
                break;
        }
        return null;
    }

    private Packet getNewInstance(final Packet packetIn) {
        if (packetIn == null) return null;
        Class<? extends Packet> packet = packetIn.getClass();
        try {
            Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate packet \"" + packetIn.getPacketID() + ", " + packet.getName() + "\".", e);
        }
    }

    public void registerPacket(EnumConnectionState connectionState, EnumPacketDirection direction, Packet packet) {
        final int packetId = packet.getPacketID();
        switch (direction) {
            case SERVERBOUND:
                switch (connectionState) {
                    case HANDSHAKE:
                        throw new IllegalArgumentException("Invalid handshake");
                    case LOGIN:
                        CLIENT_LOGIN.put(packetId, packet);
                        break;
                    case PLAY:
                        CLIENT_PLAY.put(packetId, packet);
                        break;
                    case STATUS:
                        CLIENT_STATUS.put(packetId, packet);
                        break;
                }
                break;
            case CLIENTBOUND:
                switch (connectionState) {
                    case HANDSHAKE:
                        throw new IllegalArgumentException("Invalid handshake");
                    case LOGIN:
                        SERVER_LOGIN.put(packetId, packet);
                        break;
                    case PLAY:
                        SERVER_PLAY.put(packetId, packet);
                        break;
                    case STATUS:
                        SERVER_STATUS.put(packetId, packet);
                        break;
                }
                break;
        }
    }
}
