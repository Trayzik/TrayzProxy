package pl.trayz.proxy.server.packets.server.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pl.trayz.proxy.utils.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.trayz.proxy.server.packets.Packet;
import pl.trayz.proxy.server.packets.PacketBuffer;
import pl.trayz.proxy.server.packets.server.status.status.PlayerInfo;
import pl.trayz.proxy.server.packets.server.status.status.Protocol;
import pl.trayz.proxy.server.packets.server.status.status.ServerStatusResponse;

import java.util.Arrays;
import java.util.UUID;

/**
 * @Author: Trayz
 **/

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ServerStatusResponsePacket extends Packet {

    private static final Gson gson = new GsonBuilder().create();
    private ServerStatusResponse statusInfo;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        final JsonObject jsonObject = new JsonObject();
        final JsonObject version = new JsonObject();

        String versioName = statusInfo.getVersion().getName();
        version.addProperty("name", versioName);
        version.addProperty("protocol", statusInfo.getVersion().getProtocol());
        final JsonObject players = new JsonObject();
        players.addProperty("max", statusInfo.getPlayers().getMaxPlayers());
        players.addProperty("online", statusInfo.getPlayers().getOnlinePlayers());
        if (statusInfo.getPlayers().getPlayers().length > 0) {
            final JsonArray array = new JsonArray();
            Arrays.stream(statusInfo.getPlayers().getPlayers()).forEach(gameProfile -> {
                final JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("name", gameProfile.getName());
                jsonObject1.addProperty("id", gameProfile.getUuid().toString());
                array.add(jsonObject1);
            });
            players.add("sample", array);
        }
        jsonObject.add("version", version);
        jsonObject.add("players", players);
        jsonObject.addProperty("description", statusInfo.getDescription());
        if (statusInfo.getIcon() != null) {
            jsonObject.addProperty("favicon", statusInfo.getIcon());
        }

        out.writeString(jsonObject.toString());
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        final JsonObject jsonObject = gson.fromJson(in.readStringFromBuffer(32767), JsonObject.class);
        final JsonObject version = jsonObject.get("version").getAsJsonObject();
        final Protocol versionInfo = new Protocol(version.get("name").getAsString(), version.get("protocol").getAsInt());
        final JsonObject players = jsonObject.get("players").getAsJsonObject();

        GameProfile[] gameProfiles = new GameProfile[0];
        if (players.has("sample")) {
            final JsonArray profiles = players.get("sample").getAsJsonArray();
            if (profiles.size() > 0) {
                gameProfiles = new GameProfile[profiles.size()];
                for (int index = 0; index < profiles.size(); index++) {
                    final JsonObject jsonObject1 = profiles.get(index).getAsJsonObject();
                    gameProfiles[index] = new GameProfile(UUID.fromString(jsonObject1.get("id").getAsString()),jsonObject1.get("name").getAsString());
                }
            }
        }

        final PlayerInfo playerInfo = new PlayerInfo(players.get("online").getAsInt(), players.get("max").getAsInt(), gameProfiles);
        final String description = jsonObject.get("description").getAsString();
        String icon = null;
        if (jsonObject.has("favicon")) {
            icon = jsonObject.get("favicon").getAsString();
        }

        statusInfo = new ServerStatusResponse(versionInfo, playerInfo, description, icon);
    }
}
