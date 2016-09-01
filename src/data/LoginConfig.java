package data;

import java.io.Serializable;

/**
 * Creating a separate login configuration to prevent having to re-enter login
 * credentials with every little change to the normal configuration file. This
 * class should have virtually no 
 * @author Michael Young
 */
public class LoginConfig implements Serializable {
    /**
     * Login settings
     */
    private String username;
    private String passwd;
    private String channel;
    private String currencyName;
    
    public LoginConfig() {
        username = "";
        passwd = "";
        channel = "";
        currencyName = "";
    }
    
    /**
     * Get the username from the configuration
     * @return the username from the configuration
     */
    public String getUsername() { return username; }
    
        
    /**
     * 
     * @param username
     */
    public void setUsername(String username) { this.username = username; }
    
    /**
     * Get the password from the configuration
     * @return the password from the configuration
     */
    public String getPassword() { return passwd; }
    
        
    /**
     * 
     * @param password
     */
    public void setPassword(String password) { passwd = password; }
    
    /**
     * Get the channel from the configuration
     * @return the channel from the configuration
     */
    public String getChannel() { return channel; }
    
    /**
     * 
     * @param channel
     */
    public void setChannel(String channel) { this.channel = channel; }
    
    /**
     * 
     * @return 
     */
    public String getCurrencyName() { return currencyName; }

    
    /**
     * 
     * @param currencyName
     */
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }
}
