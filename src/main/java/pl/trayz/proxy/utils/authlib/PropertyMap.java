package pl.trayz.proxy.utils.authlib;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Profile properties map.
 */
public class PropertyMap extends ForwardingMultimap<String, Property> {
    private final Multimap<String, Property> properties = LinkedHashMultimap.create();
    protected Multimap<String, Property> delegate() { return this.properties; }

}