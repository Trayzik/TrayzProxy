package pl.trayz.proxy.events;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @Author: Trayz
 **/

/**
 * PlayerPreLoginEvent is called when a player is logging (before the player is fully connected).
 */
@Data
public class PlayerPreLoginEvent {

    private final String name;
    private final Channel channel;
    private boolean onlineMode;
    private String cancelReason;
    private boolean cancelled;

    public PlayerPreLoginEvent(String name, Channel channel, boolean onlineMode) {
        this.name = name;
        this.channel = channel;
        this.onlineMode = onlineMode;
    }

    public void disallow(String reason) {
        this.cancelReason = reason;
        this.cancelled = true;
    }
}
