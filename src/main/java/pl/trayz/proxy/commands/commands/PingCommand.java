package pl.trayz.proxy.commands.commands;

import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.objects.chat.*;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.ProxyServer;
import pl.trayz.proxy.utils.ColorUtil;

/**
 * @Author: Trayz
 **/

@CommandMeta(name = "ping", aliases = {}, permission = "")
public class PingCommand extends Command {

    @Override
    public void onCommand(Player player, String[] args) {
        if(player == null) return;

        player.sendMessage(ColorUtil.fixColors("&7[&2*&7] Your ping is: &2"+player.getPing()));
    }
}
