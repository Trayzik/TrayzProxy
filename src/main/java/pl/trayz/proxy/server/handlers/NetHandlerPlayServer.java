package pl.trayz.proxy.server.handlers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.events.PlayerChatEvent;
import pl.trayz.proxy.events.PlayerCommandEvent;
import pl.trayz.proxy.events.PlayerJoinEvent;
import pl.trayz.proxy.events.api.Event;
import pl.trayz.proxy.objects.player.Player;
import pl.trayz.proxy.server.packets.INetHandler;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.client.play.ClientKeepAlivePacket;
import pl.trayz.proxy.server.packets.client.play.ClientChatPacket;
import pl.trayz.proxy.server.packets.client.play.ClientPlayerPositionPacket;

import java.util.Arrays;
import java.util.Map;

/**
 * @Author: Trayz
 **/

/**
 * Handler for packets in game.
 */
@RequiredArgsConstructor
@Data
public class NetHandlerPlayServer implements INetHandler {

    /**
     * Proxy player.
     */
    private final Player player;

    /**
     * Proxy instance.
     */
    private final ProxyApp proxyApp;

    @Override
    public void handlePacket(Packet packet) {
        if(packet instanceof ClientChatPacket) {
            /**
             * When player sends chat message.
             * Detecting commands and chat messages.
             * Calling events.
             */
            String message = ((ClientChatPacket) packet).getMessage();

            if(player.getVerification() != null && !player.getVerification().isEnteredCode()) {
                if(message.equalsIgnoreCase(player.getVerification().getCode())) {
                    player.getVerification().setEnteredCode(true);
                } else {
                    player.disconnect(proxyApp.getConfigs().getMessages().getFailedToVerificateCaptcha());
                }
                return;
            }

            if(message.startsWith("/")) {

                PlayerCommandEvent event = new PlayerCommandEvent(player,message,false);
                for(Event events : proxyApp.getRegisteredEvents()) {
                    events.playerCommandEvent(event);
                }
                if(event.isCanceled()) return;

                String cmd = message.split(" ")[0].replace("/","");

                String[] args = message.replace("/"+cmd+(message.contains(" ") ? " " : ""),"").split(" ");
                if(Arrays.toString(args).equals("[]")) args = new String[0];

                for(Map.Entry<CommandMeta, Command> command : proxyApp.getPluginLoader().getCommandMap().entrySet()) {
                    CommandMeta commandMeta = command.getKey();
                    if(commandMeta.name().equalsIgnoreCase(cmd) || Arrays.stream(commandMeta.aliases()).anyMatch(cmd::equalsIgnoreCase)) {
                        if(!commandMeta.permission().isEmpty() && !player.hasPermission(commandMeta.permission())) {
                            player.sendMessage(proxyApp.getConfigs().getMessages().getDontHavePermissionToCommand().replace("{COMMAND}",commandMeta.name()).replace("{PERMISSION}",commandMeta.permission()));
                            return;
                        }
                        command.getValue().onCommand(player,args);
                        return;
                    }
                }
            }else {
                PlayerChatEvent event = new PlayerChatEvent(player,message,false);
                for(Event events : proxyApp.getRegisteredEvents()) {
                    events.playerChatEvent(event);
                }
                if(event.isCanceled()) return;

                ((ClientChatPacket) packet).setMessage(event.getMessage());
            }
        }else if(packet instanceof ClientKeepAlivePacket) {
            /**
             * When client sends keep alive packet.
             * You can get player ping by {@link Player#getPing()}
             */
            player.setPing((int) (System.currentTimeMillis() - ((ClientKeepAlivePacket) packet).getTime()));
        }else if(packet instanceof ClientPlayerPositionPacket) {
            /**
             * Currently using only for anti-bot.
             */
            ClientPlayerPositionPacket playerPositionPacket = (ClientPlayerPositionPacket) packet;
            if(player.getVerification() != null && player.getVerification().isEnteredCode() && playerPositionPacket.getY() < 90) {
                player.setVerification(null);

                PlayerJoinEvent event = new PlayerJoinEvent(player);
                for (Event events : proxyApp.getRegisteredEvents()) {
                    events.playerJoinEvent(event);
                }

                if(event.isConnectLimbo())
                    player.connectToLimbo();
                else
                    player.connectFallback();
            }
        }

        /**
         * Forwarding packet to server.
         */
        if(player.getServerConnection() != null && player.getServerConnection().connected)
            player.getServerConnection().sendPacket(packet);
    }

}
