package pl.trayz.proxy.utils.authlib;

import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
import java.util.UUID;
import com.google.gson.TypeAdapter;

/**
 * UUID converter.
 */
public class UUIDTypeAdapter extends TypeAdapter<UUID> {

    public void write(final JsonWriter out, final UUID value) throws IOException {
        out.value(fromUUID(value));
    }
    
    public UUID read(final JsonReader in) throws IOException {
        return fromString(in.nextString());
    }
    
    public static String fromUUID(final UUID value) {
        return value.toString().replace("-", "");
    }
    
    public static UUID fromString(final String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
