package irc;

import data.*;
import logging.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * May Talos have mercy on me for this class is a mess and needs so much work.
 * Edit 1/10/2015 - This class is probably worse than when I started.
 * Edit 1/22/2015 - Still really bad, but cleaning up strings everywhere.
 * Edit 1/30/2015 - IT'S STILL BAD
 * Edit 3/28/2015 - I MUST HAVE BEEN HIGH AS FUCK WRITING THIS
 * @author Michael Young
 */
public class IRC {
    /**
     * The IRC host name.
     */
    public static final String HOST_NAME = "irc.twitch.tv";
    
    /**
     * The IRC port.
     */
    public static final int PORT = 6667;
    
    private static String nick, passwd, currency, admin, greeting, channel, user;
    
    private static final String DB_EXTENSION = ".database.dat";
    private static UserDatabase db;
    private static String dbPath;
    
    private static final String TC_EXTENSION = ".timers.dat";
    private static TimerCommands tc;
    private static String tcPath;
    
    private static final String CMD_EXTENSION = ".commands.dat";
    private static Commands cmd;
    private static String cmdPath;

    private static int interval, payout;
    private static Socket irc = null;
    
    private static BufferedReader in;
    private static InputStreamReader read;
    private static BufferedWriter out;
    private static OutputStreamWriter write;
    private static boolean greetingOn = false;
    
    /* Start of filters */
    private static boolean canMod;
    
    private static boolean capsFilter;
    private static double capsTolerance;
    private static int capsMinLength;
    
    private static boolean linksFilter;
    private static boolean bannedWordsFilter;
    
    private static boolean emotesGlobalFilter;
    private static boolean emotesSubFilter;
    private static int emoteTolerance;
    private static HashSet<String> emotes;    

    private static boolean symbolFilter;
    private static int symbolTolerance;
    /* End of filters */ 
    
    private static ListenerThread listener;
    private static PayoutThread timerThread;
    private static KeepAliveThread keepAliveThread;
    private static final ArrayList<String> CURRENT_VIEWERS = new ArrayList<>();
    private static final LinkedList<String> TEST_WORDS = new LinkedList<>();
    
    public static void init(LoginConfig lc, BotConfig bc) {
        TEST_WORDS.add("dannysucksalot");
        setCredentials(lc.getUsername(), lc.getPassword(), lc.getChannel(), lc.getCurrencyName());
        interval = bc.getPayoutInterval() * 60000;
        payout = bc.getPayoutAmount();
        
        capsFilter = bc.getCapsLockFilter();
        capsTolerance = bc.getCapsTolerance();
        capsMinLength = bc.getCapsMinLength();
        
        linksFilter = bc.getLinksFitler();
        bannedWordsFilter = bc.getBannedWordsFilter();
        
        emotesGlobalFilter = bc.getEmoteGlobalFilter();
        emotesSubFilter = bc.getEmoteSubFilter();
        emoteTolerance = bc.getEmoteTolerance();
        
        symbolFilter = false;//bc.getSymbolFilter();
        symbolTolerance = bc.getSymbolTolerance();
		emotes = new HashSet<>();
        if (emotesGlobalFilter) TwitchFilters.generateGlobalEmoteList(emotes);
        if (emotesSubFilter) TwitchFilters.generateSubEmoteList(emotes);
        
        dbPath = lc.getChannel() + DB_EXTENSION;
        try {
            db = (UserDatabase) ObjectIO.readObjectFromFile(dbPath);
        }
        catch (ClassCastException e) {
            ErrorLog.writeToLog(e);
            db = null;
        }
        if (db == null) db = new UserDatabase(IRC.channel);
        
        cmdPath = lc.getChannel() + CMD_EXTENSION;
        try {
            cmd = (Commands) ObjectIO.readObjectFromFile(cmdPath);
        }
        catch (ClassCastException e) {
            ErrorLog.writeToLog(e);
            cmd = null;
        }
        if (cmd == null) cmd = new Commands(bc.getFlag());
        
        tcPath = lc.getChannel() + TC_EXTENSION;
        try {
            tc = (TimerCommands) ObjectIO.readObjectFromFile(tcPath);
        }
        catch (ClassCastException e) {
            ErrorLog.writeToLog(e);
            tc = null;
        }
        if (tc == null) tc = new TimerCommands();
        
        if (!db.contains(admin)) {
            db.newUser(capName(admin));
            db.setUserLevel(capName(admin), UserLevel.OWNER);
        }
        
        greeting = "Welcome to the stream %s \\('.')";
    }
    
    /**
     * Set the credentials for the IRC connection.
     * @param nick Nickname of the bot.
     * @param passwd oauth token for login.
     * @param channel Channel the bot will be joining.
     * @param currency Name of the currency used by the streamer.
     * @param interval Time interval in minutes between payouts of currency.
     * @param payout Amount of currency awarded at timeout periods.
     */
    private static void setCredentials(String nick, String passwd, String channel, String currency) {
        IRC.nick = nick.toLowerCase();
        IRC.passwd = passwd;
        
        if (channel.startsWith("#")) {
            IRC.channel = channel;
            IRC.admin = capName(channel.substring(1));
        }
        else {
            IRC.channel = "#" + channel;
            IRC.admin = capName(channel);
        }
        IRC.currency = currency;
    }
    
    /**
     *  Connect to IRC
     */
    public static void connect() {   
        if (irc != null) {
            try {
                irc.close();
            }
            catch (IOException e) {
                //This shouldn't happen, but just in case we'll note it
                Console.writeLine("Could not close old irc connection.");
                ErrorLog.writeToLog(e);
            }
        }
        irc = new Socket();
        int count = 1;
        while (!isConnected()) {
            Console.writeLine("Connection attempt: " +  count);
            try {
                irc = new Socket(HOST_NAME, PORT);
                
                read = new InputStreamReader(irc.getInputStream());
                in = new BufferedReader(read);
                
                write = new OutputStreamWriter(irc.getOutputStream());
                out = new BufferedWriter(write);
                
                sendRaw("PASS " + passwd + "\n");
                sendRaw("NICK " + nick + "\n");
                sendRaw("USER " + nick + " 8 * :" + nick + "\n");
                sendRaw("JOIN " + channel + "\n");
                
                //Send a request for mods and build the modlists
                sendMessage("/mods");
            }
            catch (IOException e) {
                Console.writeLine("Unable to connect. Retrying in 5 seconds.");
            }
            if (++count > 5) {
                Console.writeLine("Can not connect to IRC. Verify your username and password. Remember the password is an oauth string.");
                disconnect();
                return;
            }
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) { /* Shouldn't happen. */ }
        }
        listener = new ListenerThread(in);
        timerThread  = new PayoutThread(interval, payout, db, CURRENT_VIEWERS);
        keepAliveThread  = new KeepAliveThread();
        listener.start();
        timerThread.start();
        keepAliveThread.start();
        tc.startCommands();
    }
    
    /**
     * Close the connection to the channel
     */
    public static void disconnect() {
        if (isConnected()) {
            listener.interrupt();
            timerThread.interrupt();
            keepAliveThread.interrupt();
            db.writeDatabase();
            ObjectIO.writeObjectToFile(cmdPath, cmd);
            tc.stopCommands();
            ObjectIO.writeObjectToFile(tcPath, tc);
            try {
                irc.close();
            }
            catch (IOException e) { /* Shouldn't happen */ }
        }
        irc = null;
    }
    
    /**
     * Checks if the IRC connection is established
     * @return true if the connection exists, false if not or irc is null
     */
    public static boolean isConnected() { return irc != null && irc.isConnected(); }
    
    /**
     * Parse the message received from IRC and deal with is as necessary
     * @param message The string received from IRC
     *  NOTE: look for a cleaner way to handle this.  This looks like shit.
     */
    public static final void parseMessage(String message) {
        if (message == null) return;
        String[] msg = message.split(" ");
        user = getUser(msg[0]);
        
        if (msg[1].equals("PRIVMSG")) {
            addUserToViewerList(user);
            msg[3] = msg[3].substring(1);
            
            /**
             * Write the input to console.
             */
            String s = "";
            for (int i = 3; i < msg.length - 1; i++) {
                s += msg[i] + " ";
            }
            s += msg[msg.length - 1];
            Console.writeLine(user + ": " + s);
            
            if (msg[3].equals("") || msg[3].charAt(0) != cmd.getFlag()) {
                //Do some moderator functions if possible
                if (db.getUserLevel(user).getLevel() <= UserLevel.SUBSCRIBER.getLevel() && (canMod || db.getUserLevel(nick).getLevel() == UserLevel.OWNER.getLevel())) {
                    String[] split = s.split(" ");
                    if (capsFilter && TwitchFilters.capsFilter(s, capsMinLength, capsTolerance) ) {
                        purge(user, user + ", http://i.imgur.com/QApFb.jpg");
                    }
                    else if (linksFilter && TwitchFilters.urlFilter(split)) {
                        purge(user, user + ", links are disabled in this chat.");
                    }
                    else if (bannedWordsFilter && TwitchFilters.bannedWordsFilter(split, TEST_WORDS)) {
                        purge(user, user + ", HEEEEEEEY YOU SAID THE SECRET WORD!");
                    }
                    else if (!emotes.isEmpty() && TwitchFilters.emotesFilter(split, emotes, emoteTolerance)) {
                        purge(user, user + ", I have nothing witty to say here.");
                    }
                    else if (symbolFilter && TwitchFilters.symbolFilter(s, symbolTolerance)) {
                        purge(user, user + " the spam is real.");
                    }
                }
                else if (user.equals("Jtv") && message.contains("moderators of this room")) {
                    String mods = "";
                    for (int i = 9; i < msg.length; i++) {
                        mods += capName(msg[i]).replace(",", "") + " ";
                    }
                    db.initMod(mods.trim().split(" "));
                    canMod = db.getUserLevel(capName(nick)).getLevel() >= UserLevel.MODERATOR.getLevel();
                }
                return;
            }
            
            msg[3] = msg[3].substring(1);
            if (msg[3].equals(Commands.ADD_COMMAND)) {
                if (db.getUserLevel(user).getLevel() < UserLevel.MODERATOR.getLevel()) {
                    sendMessage(user + " does not have permission to add commands.");
                    return;
                }
                String com = "";
                try {
                    if (msg.length >= 5 && msg[4].charAt(0) == cmd.getFlag()) {
			int i;
			for (i = 5; i < msg.length; i++) {
                            com += msg[i] + " ";
			}
                    sendMessage(cmd.addCommand(msg[4].replace("" + cmd.getFlag(), ""), com.trim()));
                    }
                    else {
                        sendMessage("Invalid parameters for !addcommand. Syntax is !addcommand ![Command Name] [Message]");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                        sendMessage("Invalid parameters for !addcommand. Syntax is !addcommand ![Command Name] [Message]");
                }
            }
            else if (msg[3].equals(Commands.ADD_TIMER)) {
                if (db.getUserLevel(user).getLevel() < UserLevel.MODERATOR.getLevel()) {
                    sendMessage(user + " does not have permission to add timers.");
                    return;
                }
                try {
                    String name = msg[4];
                    int delay = Integer.parseInt(msg[5]);
                    String output = "";
                    for (int i = 6; i < msg.length; i++) {
                        output += msg[i] + " ";
                    }
                    sendMessage(tc.addCommand(name, output, delay));
                }
                catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    sendMessage("Invalid parameters for !addtimer.  Syntax is !addtimer [Name of timer] [Delay in minutes] [Message]");
                }
            }
            else if (msg[3].equals(Commands.EDIT_COMMAND)) {

            }
            else if (msg[3].equals(Commands.EDIT_TIMER)) {

            }
            else if (msg[3].equals(Commands.REMOVE_COMMAND)) {
                try {
                    s = cmd.removeCommand(msg[4], db.getUserLevel(user)).replace(Commands.USER_FLAG, user);
                    sendMessage(s);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage("Invalid parameters for !removecommand.  Syntax is !rmcmd ![Command Name]");
                }
            }
            else if (msg[3].equals(Commands.REMOVE_TIMER)) {
                try {
                    sendMessage(tc.removeCommand(msg[4]));
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage("Invalid parameters for !removetimer.  Syntax is !rmcmd [Name of timer]");
                }
            }
            else if (msg[3].equals(Commands.DISABLE_TIMERS)) {
                tc.stopCommands();
                sendMessage("Timers have been disabled");
            }
            else if (msg[3].equals(Commands.START_TIMERS)) {
                if (tc.isRunning()) {
                    sendMessage("Timers are already running.");
                }
                else {
                tc.startCommands();
                sendMessage("Timers have been started.");
                }	
            }
            else if (msg[3].equals(Commands.GREETING)) {
                try {
                    switch (msg[4]) {
                        case "on":
                        case "true":
                        case "enable":
                        case "enabled":
                            greetingOn = true;
                            sendMessage(String.format("Greeting is set to %b", greetingOn));
                            break;
                        case "off":
                        case "false":
                        case "disable":
                        case "disabled":
                            greetingOn = false;
                            sendMessage(String.format("Greeting is set to %b", greetingOn));
                            break;
                        case "status":
                            sendMessage(String.format("Greeting is set to %b", greetingOn));
                            break;
                        case "message":
                            sendMessage(greeting);
                            break;
                        case "test":
                            sendMessage(String.format(greeting, user));
                            break;
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage("Invalid parameters for !greeting.  Syntax is !greeting [on,true,enabled,off,false,disabled,status,message]");
                }
            }
            else if (msg[3].equals(Commands.LINKS)) {
                if (db.getUserLevel(user).getLevel() < UserLevel.MODERATOR.getLevel()) return;
                try {
                    switch(msg[4]) {
                        case "on":
                        case "true":
                        case "enable":
                        case "enabled":
                            sendMessage("Links are enabled in this channel.");
                            linksFilter = false;
                            break;
                        case "off":
                        case "false":
                        case "disable":
                        case "disabled":
                            sendMessage("Links are disabled in this channel.");
                            linksFilter = true;
                            break;
                        case "status":
                            sendMessage(String.format("Links are set to %b", linksFilter));
                            break;
                        default:
                            sendMessage("Invalid parameters for !links.");
                            break;
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage("Invalid parameters for !links.");
                }
            }
            /* Figure out how to implement this cleaner */
            else if (msg[3].equals("lookup")) {
                String lookup = capName(msg[5]);
                sendMessage(String.format("User: %s %s: %d User Level: %s Is a regular: %b",
                        lookup, currency, db.getPoints(lookup), db.getUserLevel(lookup).toString().toLowerCase(), db.isRegular(lookup)));
            }
            else if (msg[3].equals("set")) {
                if (db.getUserLevel(user).getLevel() <= UserLevel.MODERATOR.getLevel() && !user.equals("Genodragon_sc")) return;
                try {
                    switch (msg[4]) {
                        case "level":
                            db.setUserLevel(capName(msg[6]), UserLevel.valueOf(msg[7].toUpperCase()));
                            ObjectIO.writeObjectToFile(dbPath, db);
                            break;
                        case "points":
                            db.changePoints(capName(msg[6]), Integer.parseInt(msg[7]));
                            break;
                        case "regular":
                            db.setRegular(msg[6], Boolean.parseBoolean(capName(msg[7]))); //Boolean parse doesn't work
                            break;
                        default:
                            throw new Exception("WHOS THAT POKEMON");
                    }
                }
                catch (Exception e) {
                    sendMessage("Invalid parameters for set.");
                }
            }
            else if (msg[3].equals("die")) {
                sendMessage("T_T Goodbye.");
                disconnect();
            }
            else if (msg[3].equals(Commands.IS_FOLLOWER)) {
                if (db.getUserLevel(user).getLevel() < UserLevel.ADMIN.getLevel()) return;
                    try {
                        if (TwitchAPI.isFollower(msg[4], admin)) {
                            sendMessage(capName(msg[4]) + " is a follower of " + admin + ".");
                        }
                        else {
                            sendMessage(capName(msg[4]) + " is not a follower of " + admin + ".");
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        sendMessage("You goofed didn't you?");
                    }
            }
            else if (msg[3].equals(Commands.IS_SUBSCRIBER)) {
                if (db.getUserLevel(user).getLevel() < UserLevel.ADMIN.getLevel()) return;
                try {
                    if (TwitchAPI.isSubscriber(msg[4], admin)) {
                        sendMessage(capName(msg[4]) + " is a subscriber of " + admin + ".");
                    }
                    else {
                        sendMessage(capName(msg[4]) + " is not a subscriber of " + admin + ".");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage("You goofed didn't you?");
                }
            }
            else if (msg[3].equals("sudo")) {
                if (db.getUserLevel(user).getLevel() <= UserLevel.MODERATOR.getLevel() || !user.equals("Genodragon_sc")) return;
                String com = "";
                for (int i = 4; i < msg.length; i++) {
                    com += msg[i] + " ";
                }
                sendMessage(com.trim());
            }
            else if (cmd.contains(msg[3])) {
                String output = cmd.getCommand(msg[3]);
                output = output.replace(Commands.USER_FLAG, user);
                sendMessage(output);	
            }
        }
        else if (msg[1].equals("JOIN")) {
            addUserToViewerList(user);
            if (greetingOn) {
                String greet = greeting.replace(Commands.USER_FLAG, user);
                sendMessage(greet);
            }
            Console.writeLine(msg[1] + " " + user);
        }
        else if (msg[1].equals("PART")) {
            removeUserFromViewerList(user);
            Console.writeLine(msg[1] + " " + user);
        }
        else if (msg[0].equals("PING")) {
            sendRaw("PONG " + msg[1]);
        }
    }
    
    /**
     * Gets the user from the input string
     * @param message A string from Twitch IRC
     * @return the username from the string.
     */
    private static String getUser(String message) { return message.split("!")[0].substring(1); }
    
    /**
     * Adds a user to the database if they do not exist and to the current viewer list.
     * The check is because PRIVMSG is used since JOIN is slow.
     * @param nick the user being added to the current database and the hash table
     */
    private static void addUserToViewerList(String nick) {
        boolean writeNeeded = false;
        if (!db.contains(nick)) {
            db.newUser(nick);
            writeNeeded = true;
        }
        if (db.getUserLevel(nick) == UserLevel.NEW && TwitchAPI.isFollower(nick, channel)) {
            db.setUserLevel(nick, UserLevel.FOLLOWER);
            writeNeeded = true;
        }
        if (writeNeeded) {
            db.writeDatabase();
        }
        synchronized (CURRENT_VIEWERS) {
            if (!CURRENT_VIEWERS.contains(nick)) {
                CURRENT_VIEWERS.add(nick);
            }
        }
    }
    
    /**
     * Removes a user from the current viewer list
     * @param nick the user being removed
     */
    private static void removeUserFromViewerList(String nick) {
        synchronized (CURRENT_VIEWERS) {
            if (CURRENT_VIEWERS.contains(nick)) {
                CURRENT_VIEWERS.remove(nick);
            }
        }
    }
    
    /**
     * Capitalizes a name given to the function.
     * @param name the name to be capitalized.
     * @return the name with the first letter capitalized.
     */
    private static String capName(String name) { return name.substring(0, 1).toUpperCase() + name.substring(1); }
    
    /**
     * Send a message to the current Twitch IRC channel.
     * @param s The message to be sent.
     */
    public static void sendMessage(String s) {
        sendRaw("PRIVMSG " + channel + " :" + s + "\n");
        Console.writeLine(nick + ": " + s);
    }
    
    /**
     * Sends a message via Twitch IRC
     * @param message the message to be sent.
     */
    public static final void sendRaw(String message) {
        //Twitch IRC uses \n as the deliminator for input, so make sure it has it.
        if (message.charAt(message.length() - 1) != '\n') {
            message += "\n";
        }
        int attempt = 0;
        do {
            try {
                out.write(message);
                out.flush();
                return;
            }
            catch (IOException e) {
                ++attempt;
                ErrorLog.writeToLog(e);
            }
        } while (attempt < 4);
    }

    /**
     * Enable or disable caps filtering.
     * @param tof True enabling caps filtering, false if disabling.
     */
    public static void setCapsLockFilterStatus(boolean tof) { capsFilter = tof; }
    
    /**
     * Enable or disable links filtering.
     * @param tof True enabling links filtering, false if disabling.
     */
    public static void setLinksFilterStatus(boolean tof) { linksFilter = tof; }
    
    /**
     * Purge the chat of a user.
     * @param user The user being purged.
     */
    private static void purge(String user, String displayMessage) {
        sendMessage("/timeout " + user + " 1");
        sendMessage(displayMessage);
    }
    
    /**
     * Time out a user from the chat for a given amount of time.
     * @param user The user being timed out.
     * @param time The time in seconds to time out.
     */
    private static void timeout(String user, int time) { sendMessage("/timeout " + user + " " + time); }
    
    /**
     * Ban the user from the chat.
     * @param user The user being banned
     */
    private static void ban(String user) { sendMessage("/ban " + user); }
}
