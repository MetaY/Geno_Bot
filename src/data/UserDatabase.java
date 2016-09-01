package data;

import logging.ObjectIO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The database class will create a database of users for the streamer to have
 * access to.  This database will keep track of points that a user has
 * accumulated, the user level, and whether they are a regular.
 * 
 * @TODO: isFollower, isSubscriber(?)
 * 
 * @author Michael Young
 */
public class UserDatabase implements Serializable {
    private final String CHANNEL;
    private final String FILE_PATH;
    private final HashMap<String, User> DB;
    private final ArrayList<String> MODS;
    private byte writeBuffer;
    
    /**
     * 
     * @param channel 
     */
    public UserDatabase(String channel) {
        this.CHANNEL = channel.substring(1);
        FILE_PATH = CHANNEL + ".database.dat";
        DB = new HashMap<>();
        MODS = new ArrayList<>();
        writeBuffer = 0;
    }
    
    /**
     * Check if a user exists in the database
     * @param key The name of the user
     * @return true if the user exists in the database, false if not
     */
    public synchronized boolean contains(String key) {
        return DB.containsKey(key);
    }
    
    /**
     * Create a new user in the database
     * @param key The name of the user
     */
    public synchronized void newUser(String key) { DB.put(key, new User()); }
    
    /**
     * Change the access level a user has to the bot
     * @param key The name of the user
     * @param level The access level the user has.
     */
    public synchronized void setUserLevel(String key, UserLevel level) {
        if (!contains(key)) {
            newUser(key);
        }
        DB.get(key).userLevel = level;
    }
    
    /**
     * Update the list of moderators and 
     * @param moderators 
     */
    public synchronized void initMod(String[] moderators) {
        //First build the list of all current moderators
        ArrayList<String> modList = new ArrayList<>();
        modList.addAll(Arrays.asList(moderators));
        
        for (int i = 0; i < modList.size(); i++) {
            if (!contains(modList.get(i))) {
                newUser(modList.get(i));
            }
            DB.get(modList.get(i)).userLevel = UserLevel.MODERATOR;
        }
        
        if (true) return;
        
        //Next, remove entries that no longer exist
        for (int i = 0; i < MODS.size(); i++) {
            if (!modList.contains(MODS.get(i))) {
                if (TwitchAPI.isFollower(MODS.get(i), CHANNEL)) {
                    DB.get(MODS.get(i)).userLevel = UserLevel.FOLLOWER;
                }
                else {
                    DB.get(MODS.get(i)).userLevel = UserLevel.NEW;
                }
                MODS.remove(modList.get(i));
            }
        }
        
        //Next, add the new entries
        for (int i = 0; i < modList.size(); i++) {
            if (!MODS.contains(modList.get(i))) {
                MODS.add(modList.get(i));
            }
        }
        
        //Finally, update the database and write it
        for (int i = 0; i < MODS.size(); i++) {
            if (!contains(MODS.get(i))) {
                newUser(MODS.get(i));
            }
            DB.get(MODS.get(i)).userLevel = UserLevel.MODERATOR;
        }
    }
    
    /**
     * Return the access level a user has to the bot. If the user is not in the
     * database, they will be added and given level NEW.
     * @param key The name of the user
     * @return the access level of a user
     */
    public synchronized UserLevel getUserLevel(String key) {
        if (!contains(key)) {
            newUser(key);
        }
        return DB.get(key).userLevel;
    }
    
    /**
     * Add or subtract points from a user
     * @param key The name of the user
     * @param points The number of points to change, positive or negative
     */
    public synchronized void changePoints(String key, long points) {
        if (!contains(key)) {
            newUser(key);
        }
        DB.get(key).points += points;
    }
    
    /**
     * Set a user's points to a given value.
     * @param key The name of the user.
     * @param points The point value.
     */
    public synchronized void setPoints(String key, long points) {
        if (!contains(key)) {
            newUser(key);
        }
        DB.get(key).points = points;
    }
    
    /**
     * Get the number of points a user has accumulated.
     * @param key The name of the user
     * @return The number of points, -1 if the user does not exist
     */
    public synchronized long getPoints(String key) {
        if (!contains(key)) {
            return -1;
        }
        return DB.get(key).points;
    }
    
    /**
     * 
     * @param key
     * @param tof 
     */
    public synchronized void setRegular(String key, boolean tof) {
        if (!contains(key)) {
            newUser(key);
        }
        DB.get(key).isRegular = tof;
    }
    
    public synchronized boolean isRegular(String key) {
        if (!contains(key)) {
            newUser(key);
        }
        return DB.get(key).isRegular;
    }
    
    /**
     * Write the database if the database has been updated enough. I added a
     * buffer to this because writing every time a user was added seems silly.
     * I added an override function that takes an object that doesn't matter
     * as parameter for disconnects and for point updates.
     */
    public synchronized void writeDatabase() { ObjectIO.writeObjectToFile(FILE_PATH, this); }
    
    /**
     * The User class is used to store the access level and points a user has.
     * The name is not necessary to store as the key in the hash map is the name
     * of the user.  This inner class is expandable, but may have issues with
     * reads if it is extended.
     * 
     * User levels are as follows
     * 0 - new person to the stream or someone that hasn't followed
     * 1 - Follower
     * 2 - Subscriber (if applicable)
     * 3 - Moderator
     * 4 - Special User to the bot
     * 5 - Owner
     * 
     */
    private class User implements Serializable {
        
        private long points;
        private UserLevel userLevel;
        private boolean isRegular;
        
        public User() {
            this.points = 0;
            this.isRegular = false;
            this.userLevel = UserLevel.NEW;
        }
    }
}
