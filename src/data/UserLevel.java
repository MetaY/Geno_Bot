package data;

/**
 * This enum will represent the possible user levels of the bot. They are in
 * order of least power to most power.
 * @author Michael Young
 */
public enum UserLevel {
    NEW(0), FOLLOWER(1), SUBSCRIBER(2), MODERATOR(3), ADMIN(4), OWNER(5);
    
    private final byte LEVEL;
    
    /**
     * Definition of the user levels in a Twitch chat.
     * @param level The level of the user level.
     */
    UserLevel(int level) { this.LEVEL = (byte) level; }
    
    /**
     * Get the level number.
     * @return The level number.
     */
    public int getLevel() { return LEVEL; }
}
