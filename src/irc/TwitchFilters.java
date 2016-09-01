package irc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.HashSet;
import logging.Console;
import logging.ErrorLog;

/**
 *
 * @author Michael Young
 */
public class TwitchFilters {
    
    /**
     * Check whether a user is using an excessive amount of capital characters
     * @param message The message being checked.
     * @param messageLength
     * @param tolerance The percentage of capital letters allowed.
     * @return True if exceeds tolerance, false if not.
     */
    public static boolean capsFilter(String message, int messageLength, double tolerance) {
        message = message.replace(" ", "");
        if (message.length() < messageLength) {
            return false;
        }
        double caps = 0;
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) >= 'A' && message.charAt(i) <= 'Z') {
                ++caps;
            }
        }
        return  caps / message.length() > tolerance;
    }
        
    /**
     * Check to see if a URL is valid.
     * @param message The message as a split message
     * @return True if the message is a URL, false if not.
     */
    public static boolean urlFilter(String[] message) {
        try {
            for (int i = 0; i < message.length;) {
                URL test = new URL(message[i]); //We only care if the exception isn't thrown. Maybe replace this with a regex?
                return true;
            }
        }
        catch (MalformedURLException e) {
            //Need to do more checks, but here's a prototype of it
        }
        return false;
    }
    
    /**
     * Checks a list of banned words to see if a message contains a banned word.
     * @param message The message being checked.
     * @param bannedWords The list of words that are banned.
     * @return True if the list contains a banned word, false if not.
     */
    public static boolean bannedWordsFilter(String[] message, List<String> bannedWords) {
        for (String m : message) {
            for (String bannedWord : bannedWords) {
                if (m.equals(bannedWord)) return true;
            }
        }
        return false;
    }
    
    /**
     * Checks a message to see if it contains too many emotes.
     * @param message The message being checked.
     * @param emotes The set of emotes, global and/or sub emotes.
     * @param tolerance The number of allowed emotes.
     * @return True if it contains too many emotes, false if not.
     */
    public static boolean emotesFilter(String[] message, HashSet<String> emotes, int tolerance) {
        int count = 0;
        for (String s : message) {
            if (emotes.contains(s) && ++count > tolerance) return true;
        }
        return false;
    }
    
    /**
     * Check a message to see if it contains too many symbols.
     * (Characters that are not alphanumeric)
     * @TODO Seems a bit too sensitive. However, I don't know if the solution is
     * to increase the tolerance or look deeper into how I implemented the
     * function in IRC.
     * @param message The message being checked.
     * @param tolernace The number of allowed symbols.
     * @return True if the message contains too many symbols, false if not.
     */
    public static boolean symbolFilter(String message, int tolernace) {
        int symbolCount = 0;
        for (int i = 0; i < message.length(); i++) {
            char a = message.charAt(i);
            if ((a < 'a' || a > 'z') && (a < 'A' || a > 'Z') && (a < '0' || a > '9') && a != ' ' && ++symbolCount > tolernace) return true;
        }
        return false;
    } 
    
    /**
     * Generate the global emote list and put it in the emote set.
     * @param emotes The set that contains emotes.
     */
    public static void generateGlobalEmoteList(HashSet<String> emotes) {
        //These global emotes aren't pulled from twitchemoves.com so add them manually
        emotes.add(":)"); emotes.add(":("); emotes.add(":0");
        emotes.add(":z"); emotes.add("B)"); emotes.add(":/");
        emotes.add(";)"); emotes.add(";p"); emotes.add(":p");
        emotes.add("R)"); emotes.add("o_O"); emotes.add(":D");
        emotes.add("<3");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://twitchemotes.com/filters/global").openStream()))) {
            String s;
            while ((s = br.readLine()) != null) {
                if (s.contains("<br />")) {
                    s = s.replace("\t", "").replace("<br />", "");
                    emotes.add(s);
                }
            }
        }
        catch (Exception e) {   //Have to catch MalformedURLException and IOException
            ErrorLog.writeToLog(e);
            Console.writeLine("Could not create global filter list.");
        }
    }
    
    /**
     * Generate the sub filter list and put it into emotes.
     * @param emotes The set that contains emotes.
     */
    public static void generateSubEmoteList(HashSet<String> emotes) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://twitchemotes.com/filters/sub").openStream()))) {
            String s;
            while ((s = br.readLine()) != null) {
                if (s.contains("<br />")) {
                    s = s.replace("\t", "").replace("<br />", "");
                    emotes.add(s);
                }
            }
        }
        catch (Exception e) {
            ErrorLog.writeToLog(e);
            Console.writeLine("Could not create subscriber filter list.");
        }
    }
}
