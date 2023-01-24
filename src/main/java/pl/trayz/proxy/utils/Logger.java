package pl.trayz.proxy.utils;

import java.text.SimpleDateFormat;

/**
 * @Author: Trayz
 **/

/**
 * Logger system.
 */
public class Logger {
    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void logInfo(String message) {
        System.out.println(simpleDateFormat.format(System.currentTimeMillis()) +" \u001B[34m[INFO] \033[0m" + message);
    }

    public static void logError(String message) {
        System.out.println(simpleDateFormat.format(System.currentTimeMillis()) +" \u001B[31m[ERROR] \033[0m" + message);
    }

    public static void logWarning(String message) {
        System.out.println(simpleDateFormat.format(System.currentTimeMillis()) +" \u001B[33m[ERROR] \033[0m" + message);
    }
}
