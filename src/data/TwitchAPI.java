package data;

import logging.ErrorLog;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class will provide functions that will interface with Twitch to
 * determine certain aspects. 
 * 
 * Link to Twitch API: https://github.com/justintv/twitch-api
 * @author Michael Young
 */

public class TwitchAPI {    
    
    /**
     * A string that represents the curl string for the Twitch API follower
     * check. Replace test_user with the user being checked and test_channel
     * is the channel the user should be following.
     */
    private static final String FOLLOWER_CHECK = "https://api.twitch.tv/kraken/users/test_user1/follows/channels/test_channel";
    
    /**
     * A string that represents the curl string for the Twitch API subscriber
     * check. Replace testuser with the user being checked and test_channel
     * is the channel the user should be following. There is a slight API
     * difference between subscribers and followers for some reason.
     */
    private static final String SUBSCRIBER_CHECK = "https://api.twitch.tv/kraken/channels/test_channel/subscriptions/testuser";
    
    /**
     * Check whether a user is following a channel.
     * @param user The user being checked.
     * @param channel The channel to check against.
     * @return True if the user is a follower, false if not.
     */
    public static boolean isFollower(String user, String channel) {
        
        URL curl;
        try {
            curl = new URL(FOLLOWER_CHECK.replace("test_user1", user).replace("test_channel", channel));
            curl.openStream();
            return true;
        }
        catch (MalformedURLException e) {
            //Shouldn't happen unless there's an API change, but just in case.
            ErrorLog.writeToLog(e);
            return false;
        }
        catch (IOException e) { /* Page does not exist. Condition we're looking for. */
            return false;
        }
    }
    
    /**
     * Check whether a user is subscribed to a channel.
     * @param user The user being checked.
     * @param channel The channel to check against.
     * @return True if the user is a subscriber, false if not.
     */
    public static boolean isSubscriber(String user, String channel) {
        
        URL curl;
        try {
            curl = new URL(SUBSCRIBER_CHECK.replace("test_channel", channel).replace("testuser", user));
            curl.openStream();
            return true;
        }
        catch (MalformedURLException e) {
            //Shouldn't happen, but be safe.
            ErrorLog.writeToLog(e);
            return false;
        }
        catch (IOException e) { /* Link doesn't exist. Condition being checked for. */
            return false;
        }
    }    
}
