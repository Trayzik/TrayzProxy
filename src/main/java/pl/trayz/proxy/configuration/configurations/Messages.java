package pl.trayz.proxy.configuration.configurations;

import lombok.Data;
import pl.trayz.proxy.configuration.api.annotations.Config;

/**
 * @Author: Trayz
 **/

/**
 * Default proxy config with messages
 */
@Config(
        file = "messages.json"
)
@Data
public class Messages {

    private String serverIsFull;
    private String mojangAuthenticationFailed;
    private String incorrectMinecraftVersion;
    private String dontHavePermissionToCommand;
    private String failedConnectToServer;
    private String arleadyConnectedToServer;
    private String invalidNickname;
    private String youAreArleadyConnectedToProxy;
    private String failedToVerificateCaptcha;
    private String pleaseEnterCaptcha;
    private String captcha;

    public Messages() {
        this.serverIsFull = "§7[§2*§7] §7Server is §2full§7!";
        this.mojangAuthenticationFailed = "§7[§2*§7] §7Failed to §2authenticate §7with §2mojang§7!";
        this.incorrectMinecraftVersion = "§7[§2*§7] §7Incorrect game §2version§7!";
        this.dontHavePermissionToCommand = "§7[§2*§7] §7You §2don't §7have §2permission §7to use §2/{COMMAND} §7(§c{PERMISSION}§7)";
        this.failedConnectToServer = "§7[§2*§7] §7Failed to §2connect §7to §2{SERVER}§7!";
        this.arleadyConnectedToServer = "§7[§2*§7] §7You are §2already connected §7to §2{SERVER}§7!";
        this.invalidNickname = "§7[§2*§7] §7Your §2nickname §7is §2invalid§7!";
        this.youAreArleadyConnectedToProxy = "§7[§2*§7] §7You are §2already connected §7to §2proxy§7!";
        this.failedToVerificateCaptcha = "§7[§2*§7] §7Failed to verificate §2captcha!";
        this.pleaseEnterCaptcha = "§7Please complete the captcha to continue";
        this.captcha = "§7[§2*§7] §7Your captcha question is: §2{QUESTION}";
    }

}
