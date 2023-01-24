package pl.trayz.proxy.objects.chat;

/**
 * @Author: Trayz
 **/

/**
 * Format of chat message
 */
public enum ChatFormat {
    BOLD,
    UNDERLINED,
    STRIKETHROUGH,
    ITALIC,
    OBFUSCATED;

    /**
     * Get enum by name
     */
    public static ChatFormat byName(String name) {
        name = name.toLowerCase();
        for (ChatFormat format : values()) {
            if (format.toString().equals(name)) {
                return format;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}