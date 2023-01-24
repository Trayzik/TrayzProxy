package pl.trayz.proxy.enums;

/**
 * @Author: Trayz
 **/

/**
 * Cause of player connecting to server (event)
 */
public enum PlayerServerConnectCause {

    /**
     * When player connect to proxy
     */
    JOIN_PROXY,

    /**
     * When player was connected to proxy and changing server
     */
    CHANGE_SERVER
}
