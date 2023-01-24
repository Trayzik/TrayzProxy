package pl.trayz.proxy.objects.player;

import lombok.Data;

/**
 * @Author: Trayz
 **/

/**
 * Captcha verification parameters
 */
@Data
public class Verification {

    /**
     * Time of verification
     */
    private long time;

    /**
     * Generated captcha code
     */
    private String code;

    /**
     * Is captcha code verified
     */
    private boolean enteredCode;

    /**
     * Constructor
     * @param code generated verification code
     */
    public Verification(String code) {
        this.time = System.currentTimeMillis();
        this.code = code;
        this.enteredCode = false;
    }
}
