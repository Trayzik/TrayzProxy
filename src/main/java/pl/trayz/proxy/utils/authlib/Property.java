package pl.trayz.proxy.utils.authlib;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Profile property object.
 */

@Data
@AllArgsConstructor
public class Property {
    private final String name;
    private final String value;
    private final String signature;

    public Property(String value, String name) {
        this(value, name, null);
    }

    public boolean hasSignature() { return (this.signature != null); }

}