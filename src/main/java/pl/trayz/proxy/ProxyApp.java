package pl.trayz.proxy;

import lombok.Getter;
import pl.trayz.proxy.cache.Cache;
import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.commands.commands.AboutCommand;
import pl.trayz.proxy.configuration.Configs;
import pl.trayz.proxy.configuration.configurations.Config;
import pl.trayz.proxy.enums.EnumConnectionState;
import pl.trayz.proxy.enums.EnumPacketDirection;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.chat.Message;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.plugins.PluginLoader;
import pl.trayz.proxy.server.MinecraftServer;
import pl.trayz.proxy.server.ProxyServer;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketsManager;
import pl.trayz.proxy.threads.RegisterThreads;
import pl.trayz.proxy.utils.ColorUtil;
import pl.trayz.proxy.utils.Dispatcher;
import pl.trayz.proxy.utils.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * @Author: Trayz
 **/

@Getter
public class ProxyApp extends ProxyServer {

    private final List<Player> players = new ArrayList<>();
    private final List<Event> registeredEvents = new ArrayList<>();
    private final PacketsManager packetsManager = new PacketsManager();
    private final Configs configs;
    private final PluginLoader pluginLoader;

    /**
     * Application loader
     * You can load application from your jar file
     * If you don't want to create plugins folder, configurations files you can set external to true
     */
    public ProxyApp(boolean external) {
        java.util.logging.Logger.getLogger("io.netty").setLevel(Level.OFF);

        this.configs = new Configs(external);

        Dispatcher.setService(Executors.newFixedThreadPool(configs.getConfig().getThreads()));

        String host = configs.getConfig().getHost();
        new MinecraftServer(host.split(":")[0],Integer.parseInt(host.split(":")[1]),this);
        new RegisterThreads(this);

        this.pluginLoader = new PluginLoader(external);

        new Thread(this::consoleReader).start();
    }

    /**
     * Command reader from console
     */
    private void consoleReader() {
        Scanner in = new Scanner(System.in);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String cmd = line.split(" ")[0];

            String[] args = line.replace(cmd+(line.contains(" ") ? " " : ""),"").split(" ");
            if(Arrays.toString(args).equals("[]")) args = new String[0];

            boolean found = false;

            for(Map.Entry<CommandMeta, Command> command : pluginLoader.getCommandMap().entrySet()) {
                CommandMeta commandMeta = command.getKey();
                if(commandMeta.name().equalsIgnoreCase(cmd) || Arrays.stream(commandMeta.aliases()).anyMatch(cmd::equalsIgnoreCase)) {
                    command.getValue().onCommand(null,args);
                    found = true;
                    break;
                }
            }

            if(!found) Logger.logInfo("Command not found!");
        }
    }

    /**
     * Connected players list
     */
    @Override
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Register event
     */
    @Override
    public void registerEvent(Event event) {
        registeredEvents.add(event);
    }

    /**
     * Unregister registered event
     */
    @Override
    public void unregisterEvent(Event event) {
        registeredEvents.remove(event);
    }

    /**
     * Get list of registered events
     */
    @Override
    public List<Event> registeredEvents() {
        return registeredEvents;
    }


    /**
     * Unregister registered command
     */
    @Override
    public void unregisterCommand(String name) {
        pluginLoader.getCommandMap().keySet().stream().filter(key -> key.name().equalsIgnoreCase(name)).findFirst().ifPresent(key -> pluginLoader.getCommandMap().remove(key));
    }

    /**
     * Register command without using annotation
     */
    @Override
    public void registerCommand(String name, String[] aliases, String permission, Command command) {
        pluginLoader.getCommandMap().put(new CommandMeta() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CommandMeta.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String[] aliases() {
                return aliases;
            }

            @Override
            public String permission() {
                return permission;
            }
        },command);
    }

    /**
     * Get list of registered commands
     */
    @Override
    public Map<CommandMeta,Command> registeredCommands() {
        return pluginLoader.getCommandMap();
    }

    /**
     * Get proxy configurations
     */
    @Override
    public Configs getConfigurations() {
        return configs;
    }

    /**
     * Adding server to cache
     */
    @Override
    public void addServer(String name, String host) {
        configs.getServers().getServers().put(name,host);
    }

    /**
     * Removing server from cache
     */
    @Override
    public void removeServer(String name) {
        configs.getServers().getServers().remove(name);
    }

    /**
     * Get list of servers
     */
    @Override
    public Map<String, String> getServers() {
        return configs.getServers().getServers();
    }

    /**
     * Broadcast message to all online players on proxy
     */
    @Override
    public void broadcastMessage(String message) {
        this.broadcastMessage(Message.fromString(message), null);
    }

    /**
     * Broadcast message to all online players with permission on proxy
     */
    @Override
    public void broadcastMessage(String message, String permission) {
        this.broadcastMessage(Message.fromString(message), permission);
    }

    /**
     * Broadcast message with format to all online players on proxy
     */
    @Override
    public void broadcastMessage(Message message) {
        this.broadcastMessage(message, null);
    }

    /**
     * Broadcast message with format to all online players with permission on proxy
     */
    @Override
    public void broadcastMessage(Message message, String permission) {
        this.players.forEach(player -> {
            if(Objects.isNull(permission) || player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        });
    }

    /**
     * Get online player by name
     */
    @Override
    public Player getPlayer(String name) {
        return players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Get online player by uuid
     */
    @Override
    public Player getPlayer(UUID uuid) {
        return players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Get proxy port
     */
    @Override
    public int getPort() {
        return Integer.parseInt(configs.getConfig().getHost().split(":")[1]);
    }

    /**
     * Get max players on proxy
     */
    @Override
    public int getMaxPlayers() {
        return configs.getConfig().getMaxPlayers();
    }

    /**
     * Set max players on proxy
     */
    @Override
    public void setMaxPlayers(int maxPlayers) {
        configs.getConfig().setMaxPlayers(maxPlayers);
    }

    /**
     * Register minecraft packet
     */
    @Override
    public void registerPacket(EnumConnectionState connectionState, EnumPacketDirection direction, Packet packet) {
        packetsManager.registerPacket(connectionState,direction,packet);
    }

    /**
     * Shutdown proxy
     */
    @Override
    public void shutdown(String reason) {
        this.registeredEvents.forEach(Event::proxyDisableEvent);
        this.players.forEach(player -> player.disconnect(ColorUtil.fixColors(reason)));

        Logger.logInfo("Proxy has been disabled by plugin!");
        System.exit(1);
    }

    @Override
    public boolean isServerExists(String name) {
        return getServers().containsKey(name);
    }

    @Override
    public void blockIP(String ip) {
        if(Cache.blocked.contains(ip)) return;

        Cache.blocked.add(ip);
    }

    @Override
    public boolean unblockIP(String ip) {
       return Cache.blocked.remove(ip);
    }

    @Override
    public List<String> getBlockedIps() {
        return Cache.blocked;
    }

}
