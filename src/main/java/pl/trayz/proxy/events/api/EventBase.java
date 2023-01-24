package pl.trayz.proxy.events.api;

import pl.trayz.proxy.events.*;

/**
 * @Author: Trayz
 **/

/**
 * EventBase is a base class for all events.
 */
public interface EventBase{

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
}
