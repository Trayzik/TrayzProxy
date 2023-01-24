package pl.trayz.proxy.objects.chat;

/**
 * @Author: Trayz
 **/

/**
 * Hover on message action types
 */
public enum HoverAction {
    SHOW_TEXT,
    SHOW_ITEM,
    SHOW_ACHIEVEMENT,
    SHOW_ENTITY;

    /**
     * Get enum by name
     */
    public static HoverAction byName(String name) {
        name = name.toLowerCase();
        for (HoverAction action : values()) {
            if (action.toString().equals(name)) {
                return action;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
