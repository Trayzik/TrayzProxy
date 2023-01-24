package pl.trayz.proxy.configuration.configurations;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Trayz
 **/

/**
 * Default proxy config for proxy settings
 */
@pl.trayz.proxy.configuration.api.annotations.Config(
        file = "config.json"
)
@Data
public class Config {

    private final String host;
    private final boolean onlineMode;
    private int maxPlayers;
    private final boolean ipForwarding;
    private final boolean proxyProtocol;
    private final boolean logPings;
    private final boolean logConnections;
    private final String motd;
    private final boolean captchaForNonPremium;
    private final boolean captchaForPremium;
    private final int threads;
    private final boolean connectionLimitForIP;
    private final AntiCountry antiCountry;

    public Config() {
        this.host = "0.0.0.0:25565";
        this.maxPlayers = 5000;
        this.onlineMode = true;
        this.logPings = true;
        this.logConnections = true;
        this.ipForwarding = true;
        this.proxyProtocol = false;
        this.motd = "§7[§2*§7] §7This is §2default motd§7!";
        this.captchaForNonPremium = true;
        this.captchaForPremium = false;
        this.threads = 3;
        this.connectionLimitForIP = true;
        this.antiCountry = new AntiCountry();
    }

    @Data
    public static class AntiCountry {
        private boolean enabled;
        private List<String> allowedCountries;
        private List<String> allowedIps;

        public AntiCountry() {
            this.enabled = true;
            this.allowedCountries = List.of("PL", "DE", "GB");
            this.allowedIps = List.of("127.0.0.1");
        }
    }
}
