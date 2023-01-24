<p align="center">
  <img src="https://img.shields.io/badge/Made%20with-Java-1f425f.svg?style=flat-square"/>
</p>

# TrayzProxy
This is a sophisticated proxy to teleport players between multiple minecraft servers. One of the advantages of this proxy is the extensive and easy-to-use API.

## Benefits ➕

- Multithreading
- Extensive and easy to use api
- Simple antibot
- Limbo
- Country filter
- Ability to run a proxy from within your own application
- Easy possibility to add new features (e.g. other packets)
- Full configurable

# Developers ⚒

## Maven
Repository:
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
  ```
Dependency:
```xml
<dependency>
  <groupId>com.github.Trayzik</groupId>
  <artifactId>TrayzProxy</artifactId>
  <version>1.0</version>
</dependency>
  ```

## Plugin main
In main class you need to add annotation with parameters: name, authors, version, description
```java
@ProxyPlugin(name = "YourPluginName", authors = {"YourName"}, version = "VersionOfPlugin",description = "Description of plugin") 
```
Your start method is your class constructor.
Ps. if you want to get proxy method just use ``ProxyServer.getInstance()``
## Commands
In your command class you need to add annotation @CommandMeta with parameters: name, aliases, permission(empty if without) and you need to extend class `Command`
E.g usage:
```java
@CommandMeta(name = "yourcommand", aliases = {"yourcommandalias","yourcommandalias2"}, permission = "")
public class YourCommand extends Command {

    @Override
    public void onCommand(Player player, String[] args) {
    
    }
}
```
Ps. player is null when console is commandsender

Feature:
If you don't want to use annotation to register your command, you can register it manualy:
``ProxyServer.getInstance().registerCommand("Command", new String[]{"aliases"}, "permission", new YourCommand());``
## Events
In your event class you need to extend class `Event` and add `@Override` before void with event. 
E.g usage:
```java
public class YourEventClass extends Event {

    @Override
    public void playerHandshakeEvent(PlayerHandshakeEvent event) {
      
    }
 }
```
After this steps you need to register your event using: ``ProxyServer.getInstance().registerEvent(new YourEventClass());``

List of events:
```java
    /**
     * PlayerPreLoginEvent is called when a player is logging (before the player is fully connected).
     */
    default void playerPreLoginEvent(PlayerPreLoginEvent event) {}

    /**
     * ProxyPingEvent is called when a client is pinging the proxy.
     */
    default void proxyPingEvent(ProxyPingEvent event) {}

    /**
     * PlayerQuitEvent is called when a player disconnect from the proxy.
     */
    default void playerQuitEvent(PlayerQuitEvent event) {}

    /**
     * PlayerJoinEvent is called when a player join the proxy (after PlayerPreLoginEvent).
     */
    default void playerJoinEvent(PlayerJoinEvent event) {}

    /**
     * PlayerServerConnectEvent is called when a player is connecting to a server.
     */
    default void playerServerConnectEvent(PlayerServerConnectEvent event) {}

    /**
     * PlayerPermissionCheckEvent is called when a player is checking permission.
     */
    default void playerPermissionCheckEvent(PlayerPermissionCheckEvent event) {}

    /**
     * PlayerCommandEvent is called when a player is executing a command.
     */
    default void playerCommandEvent(PlayerCommandEvent event) {}

    /**
     * PlayerChatEvent is called when a player send a message on chat.
     */
    default void playerChatEvent(PlayerChatEvent event) {}

    /**
     * PlayerHandshakeEvent is called when a player is sending a handshake (Only login, this event is called before PlayerPreLoginEvent).
     */
    default void playerHandshakeEvent(PlayerHandshakeEvent event) {}

    /**
     * ProxyDisableEvent is called when the proxy is shutting down (By end command).
     */
    default void proxyDisableEvent() {}
   ```
## Configurations
#### Initialize config loader
```java
Configuration configuration = new Configuration();
```

#### Load config
```java
configuration.load(YourCfg.class,new YourCfg());
```

#### Save config
```java
configuration.save(YourCfg.class, yourCfgClass);
```

#### Example configuration:
```java
@Config(file = "yourcfg.json")
public class YourCfg {
    
    private String myString;
    private boolean myBoolean;
    
    public YourCfg(String myString, boolean myBoolean) {
        this.myString=myString;
        this.myBoolean=myBoolean;
    }
}
```

## License❤️

Copyright © 2023 [Trayz](https://github.com/Trayzik).<br />
