package pl.trayz.proxy.server;

import lombok.Getter;
import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.configuration.Configs;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.EnumPacketDirection;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.chat.Message;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.packets.Packet;

import java.util.*;

/**
 * @Author: Trayz
 **/

/**
 * Abstract class with basic methods
 */
public abstract class ProxyServer {

    @Getter private static ProxyServer instance;

    public ProxyServer() {
        instance = this;
    }

    public abstract List<Player> getPlayers();
    public abstract void registerEvent(Event event);
    public abstract void unregisterEvent(Event event);
    public abstract List<Event> registeredEvents();

    public abstract void unregisterCommand(String name);
    public abstract void registerCommand(String name,String[] aliases,String permission, Command command);

    public abstract Map<CommandMeta,Command> registeredCommands();
    public abstract Configs getConfigurations();

    public abstract void addServer(String name, String host);

    public abstract void removeServer(String name);
    public abstract Map<String, String> getServers();


    public abstract void broadcastMessage(String message);
    public abstract void broadcastMessage(String message, String permission);
    public abstract void broadcastMessage(Message message);
    public abstract void broadcastMessage(Message message, String permission);

    public abstract Player getPlayer(String name);
    public abstract Player getPlayer(UUID uuid);

    public abstract int getPort();

    public abstract int getMaxPlayers();

    public abstract void setMaxPlayers(int maxPlayers);

    public abstract void registerPacket(EnumConnectionState connectionState, EnumPacketDirection direction, Packet packet);

    public abstract void shutdown(String reason);

    public abstract boolean isServerExists(String name);

    public abstract void blockIP(String ip);
    public abstract boolean unblockIP(String ip);
    public abstract List<String> getBlockedIps();
}
