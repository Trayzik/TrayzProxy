package pl.trayz.proxy.commands.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Trayz
 **/

/**
 * Annotation for command information
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMeta {

    /**
     * Command name
     */
    String name();

    /**
     * Command aliases
     */
    String[] aliases();

    /**
     * Command permission, if empty, no permission is required
     */
    String permission();
}
