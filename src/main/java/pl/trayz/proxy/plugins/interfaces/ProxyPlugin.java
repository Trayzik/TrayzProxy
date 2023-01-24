package pl.trayz.proxy.plugins.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Trayz
 **/

/**
 * Main annotation for plugins
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyPlugin {

    /**
     * Plugin name
     */
    String name();

    /**
     * Plugin authors
     */
    String[] authors();

    /**
     * Plugin version
     */
    String version();

    /**
     * Plugin description
     */
    String description();
}
