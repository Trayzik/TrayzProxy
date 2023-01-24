package pl.trayz.proxy.utils.authlib;

import lombok.Data;

import java.util.UUID;

/**
 * Premium player profile
 */
@Data
public class GameProfile {

    private final PropertyMap properties;
    private final String name;
    private final UUID uuid;

    public GameProfile(final UUID id, final String name) {
        this.properties = new PropertyMap();
        this.uuid = id;
        this.name = name;
    }
}
