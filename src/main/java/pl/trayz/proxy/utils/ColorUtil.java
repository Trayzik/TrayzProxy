package pl.trayz.proxy.utils;

/**
 * @Author: Trayz
 **/

/**
 * Simple class for converting & to §
 */
public class ColorUtil {
    public static String fixColors(String text) {
        return text.replace("&", "§");
    }
}
