package pl.trayz.proxy.configuration;

import lombok.Data;
import lombok.Getter;
import pl.trayz.proxy.configuration.api.Configuration;
import pl.trayz.proxy.configuration.configurations.Config;
import pl.trayz.proxy.configuration.configurations.Messages;
import pl.trayz.proxy.configuration.configurations.Servers;
import pl.trayz.proxy.utils.Logger;

import java.io.IOException;

/**
 * @Author: Trayz
 **/

/**
 * Default proxy configs loader
 */

@Data
public class Configs {

    private Config config;
    private Servers servers;
    private Messages messages;

    public Configs(boolean external) {
        Configuration configLoader = new Configuration();

        this.config = new Config();
        this.servers = new Servers();
        this.messages = new Messages();

        if(external) return;

        try {
            this.servers = configLoader.load(Servers.class,servers);
            this.config = configLoader.load(Config.class,config);
            this.messages = configLoader.load(Messages.class,messages);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logError("Failed to load configs");
        }
    }
}
