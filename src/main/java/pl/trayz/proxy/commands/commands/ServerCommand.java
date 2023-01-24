package pl.trayz.proxy.commands.commands;

import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.chat.*;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.ProxyServer;
import pl.trayz.proxy.utils.ColorUtil;

import java.util.Map;

/**
 * @Author: Trayz
 **/

@CommandMeta(name = "server", aliases = {"servers"}, permission = "trayzproxy.commands.server")
public class ServerCommand extends Command {

    @Override
    public void onCommand(Player player, String[] args) {
        if(player == null) return;

        if(args.length == 0) {
            player.sendMessage(ColorUtil.fixColors("&7[&2*&7] Servers:"));
            for (String server : ProxyServer.getInstance().getServers().keySet()) {
                Message message = Message.fromString(ColorUtil.fixColors("&7- &2" + server));
                MessageStyle messageStyle = new MessageStyle();
                messageStyle.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND, "/server " + server));
                messageStyle.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT, Message.fromString(ColorUtil.fixColors("&7Click to &2join&7!"))));
                message.setStyle(messageStyle);
                player.sendMessage(message);
            }
        }else {
            String server = args[0];
            if(ProxyServer.getInstance().getServers().containsKey(server)) {
                player.connect(server);
            }else {
                player.sendMessage(ColorUtil.fixColors("&7[&2*&7] Server &2server1 &7 doesn't exist!"));
            }
        }
    }
}
