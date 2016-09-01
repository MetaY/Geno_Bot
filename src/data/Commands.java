package data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * A command database of user defined commands for the channel.
 * @author Michael Young
 */
public class Commands implements Serializable {
    /**
     * Creating flags so that I can change the syntax of commands as needed.
     */
    
    private char flag;
    
    public static final String ADD_COMMAND = "addcmd";
    public static final String ADD_TIMER = "addtimer";

    public static final String EDIT_COMMAND = "editcmd";
    public static final String EDIT_TIMER = "edittimer";
    
    public static final String REMOVE_COMMAND = "rmcmd";
    public static final String REMOVE_TIMER = "rmtimer";
    
    public static final String START_TIMERS = "starttimers";
    public static final String DISABLE_TIMERS = "disabletimers";
    
    public static final String GREETING = "greeting";
    public static final String LINKS = "links";
    
    public static final String IS_FOLLOWER = "isfollower";
    public static final String IS_SUBSCRIBER = "issub";
    
    public static final String SUDO = "sudo";
    public static final String PURGE = "purge";
    
    /**
     * Flags for user create commands
     */
    public static final String USER_FLAG = "$user";
       
    private static final String FILE_NAME = ".commands.dat";
    private HashMap<String, Command> dataset;
    
    /**
     * Create a new command database using a HashMap for O(1) lookup speed. 
     * Probably not worth the space trade off, but it should be so minimal that
     * it shouldn't matter in the long run.
     * @param flag The flag being used to denote commands. ! is default.
     */
    public Commands(char flag) {
        this.flag = flag;
        dataset = new HashMap<>();
        /**
         *  Using symbols to be used for text replacements.  Because we are 
         *  adding some performance by implementing dataset as a hash map, 
         *  finding the commands should be O(1) versus the old implementation
         *  with an ArrayList that was O(n) worst case.  
         * 
         *  Certain commands, such as adding points, will not be able to be
         *  implemented this way, to my knowledge at least.
         * 
         *  Symbols used for replacement will be Unix-like in order to prevent any
         *  accidental issues
         *  $user - user issuing command
         *  $level - access level of the user
         *  $points - points the user has
         *  $touser - A parameter in the command
         */
            
        /** These are personal commands for either testing or easter eggs.  They
        *  can be overridden by calling !addcommand <command> <args>....
        */
        dataset.put("a", new Command("Hi $user \\('.')"));
        dataset.put("uw0tm8", new Command("u w0t m8?  I'll foking wreck ya I will. Swear on me mum."));
        dataset.put("art", new Command("<3 <3 <3 <3 <3 <3 I love you Art <3 <3 <3 <3 <3 <3"));
        dataset.put("wat", new Command(" ¯\\_(ツ)_/¯"));
    }
    
    /**
     * Add a new command to the command database.  Level 0 will be assumed. The
     * database will be checked as well to see if the command was already in
     * the database and it will be updated if so. (This may change later to add
     * an !editcommand command)
     * 
     * @param key The name of the command.
     * @param output The message that will be displayed when command is issued
     * @return A confirmation if the command was added or updated.
     */
    public String addCommand(String key, String output) { return addCommand(key, output, 0); }
    
    /**
     * Add a new command to the command database.  Level 0 will be assumed. The
     * database will be checked as well to see if the command was already in
     * the database and it will be updated if so. (This may change later to add
     * an !editcommand command)
     * 
     * @param key The name of the command
     * @param output The message that will be displayed when command is issued
     * @param level The level required to issue the command
     * @return A confirmation if the command was added or updated.
     */
    public String addCommand(String key, String output, int level) {
        if (dataset.containsKey(key)) {
            return editCommand(key, output, level);
        }
        else {
            dataset.put(key, new Command(output, level));
            return "Command added successfully.";
        }
    }
    
    /**
     * 
     * @param key
     * @param output
     * @param level
     * @return 
     */
    public String editCommand(String key, String output, int level) {
        Command c = dataset.get(key);
        c.output = output;
        c.level = (byte) level;
        dataset.replace(key, c);
        return "Command already exists. Updating the output.";
    }
    
    /**
     * Remove a command from the database.
     * @param key The name of the command
     * @param level The level of the user issuing the command.  Must be at
     * at least level 2 to use it.
     * @return Whether the command was removed or not.
     */
    public String removeCommand(String key, UserLevel level) {
        if (level.compareTo(UserLevel.MODERATOR) < 0) {
            return USER_FLAG + " does not have high enough permission level to remove commands.";
        }
        if (dataset.containsKey(key)) {
            dataset.remove(key);
            return "Command successfully removed.";
        }
        return "Command does not exist.";
    }
    
    /**
     * Get a command output from the database.
     * @param key The name of the command
     * @return The output of the command, or a message saying it doesn't exist.
     */
    public String getCommand(String key) {
        if (dataset.containsKey(key)) {
            return dataset.get(key).output;
        }
        return "Command does not exist.";
    }
    
    public boolean contains(String key) { return dataset.containsKey(key); }
    
    /**
     * 
     * @param key
     * @return 
     */
    public int commandLevel(String key) { return dataset.get(key).level; }
    
    public char getFlag() { return flag; }
    
    public void setFlat(char flag) {
        if ((flag >= 'a' && flag <= 'z') || (flag >= 'A' && flag <= 'Z') || (flag >= '0' && flag <= '9') || flag == '/') return;
        this.flag = flag;
    }
    
    /**
     * 
     */
    private class Command implements Serializable {
        private String output;
        private byte level;
        
        /**
         * Create a new command with the given output string and assume the 
         * access level is 0
         * 
         * @param output The string that will be displayed
         */
        public Command(String output) { this(output, 0); }
        
        /**
         * Create a new command with the given output string and level to
         * issue the command.
         * 
         * @param output The string that will be displayed
         * @param level The level required to access the command
         */
        public Command(String output, int level) {
            this.output = output;
            this.level = (byte) level;
        }
    }
}
