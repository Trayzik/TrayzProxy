package pl.trayz.proxy.objects.chat;

/**
 * @Author: Trayz
 **/

/**
 * Colors on chat
 */
public enum ChatColor {
    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE,
    RESET;

    /**
     * Get enum by name
     */
    public static ChatColor byName(String name) {
        name = name.toLowerCase();
        for (ChatColor color : values()) {
            if (color.toString().equals(name)) {
                return color;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
