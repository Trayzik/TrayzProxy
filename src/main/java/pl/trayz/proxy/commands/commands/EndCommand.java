package pl.trayz.proxy.commands.commands;

import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.ProxyServer;
import pl.trayz.proxy.utils.ColorUtil;
import pl.trayz.proxy.utils.Logger;

/**
 * @Author: Trayz
 **/

@CommandMeta(name = "end", aliases = {}, permission = "trayzproxy.commands.end")
public class EndCommand extends Command {

    @Override
    public void onCommand(Player player, String[] args) {
        String message = "&7[&2*&7] Proxy is &2shutting down&7!";
        if(args.length > 0) {
            message = String.join(" ", args);
        }

        for(Event event : ProxyServer.getInstance().registeredEvents()) {
            event.proxyDisableEvent();
        }

        for(Player players : ProxyServer.getInstance().getPlayers()) {
            players.disconnect(ColorUtil.fixColors(message));
        }

        sendConsoleLog("Proxy has been disabled by "+(player != null ? player.getName() : "CONSOLE"));
        System.exit(1);
    }
}
