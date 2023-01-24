package pl.trayz.proxy.configuration.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Trayz
 **/

/**
 * Config annotation with file path
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Config {

    /**
     * Path to config file
     */
    String file() default "config.json";
}
