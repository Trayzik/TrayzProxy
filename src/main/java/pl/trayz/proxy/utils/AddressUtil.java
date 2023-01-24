package pl.trayz.proxy.utils;

import com.google.common.base.Preconditions;
import pl.trayz.proxy.plugins.interfaces.ProxyPlugin;

import java.net.Inet6Address;
import java.net.InetSocketAddress;

/**
 * @Author: Trayz
 **/

/**
 * Method for converting address to string (Using when spigot have bungeecord function on true in config)
 */
public class AddressUtil {
    public static String sanitizeAddress(final InetSocketAddress address) {
        Preconditions.checkArgument(!address.isUnresolved(), "Unresolved address");
        final String string = address.getAddress().getHostAddress();
        if (address.getAddress() instanceof Inet6Address) {
            final int strip = string.indexOf(37);
            return (strip == -1) ? string : string.substring(0, strip);
        }
        return string;
    }
}
