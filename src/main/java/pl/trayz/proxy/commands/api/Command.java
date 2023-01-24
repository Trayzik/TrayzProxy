package pl.trayz.proxy.commands.api;

import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.utils.Logger;

/**
 * @Author: Trayz
 **/

public abstract class Command {

    /**
     * Invoke command, if player is null, command was executed from console
     */
    public abstract void onCommand(Player player, String[] args);

    /**
     * Send log to console
     */
    public void sendConsoleLog(String log) {
        Logger.logInfo(log);
    }

    public void sendMessage(Player player,String text) {
        if(player == null)
            sendConsoleLog(text);
        else
            player.sendMessage(text);
    }
}
