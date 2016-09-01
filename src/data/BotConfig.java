package data;

import java.io.Serializable;

/**
 * This class represents the settings given by a user to log into IRC. These
 * settings are saved as this object and are loaded upon launching the program
 * if settings from a previous session.
 * @author Michael Young
 */
public final class BotConfig implements Serializable {
    
    /**
     * Bot settings
     */
    private char flag;
    private String currencyName;
    private int payoutInterval;
    private int payoutAmount;
    private boolean greeting;
    
    //Caps filter
    private boolean capsFilter;
    private double capsTolerance;
    private int capsMinLength;
    
    private boolean linksFilter;
    
    private boolean bannedWords;
    
    private boolean emoteGlobalFilter;
    private boolean emoteSubFilter;
    private int emotesTolerance;
    
    private boolean symbolFilter;
    private int symbolTolerance;
    
    /**
     * A blank configuration with default settings if the configuration cannot
     * be read from file.
     */
    public BotConfig() { setDefault(); }
    
    /**
     * Set default values for the bot to take if the config file is nonexistant
     * or the user designates a reset.
     */
    public final void setDefault() {
        flag = '!';
        currencyName = "Points";
        payoutInterval = 0;
        payoutAmount = 0;
        capsFilter = true;
        capsTolerance = .75;
        capsMinLength = 10;
        linksFilter = true;
        bannedWords = false;
        greeting = false;
        emoteGlobalFilter = true;
        emoteSubFilter = true;
        emotesTolerance = 5;
        symbolFilter = true;
        symbolTolerance = 10;
    }
    
    /**
     *
     * @return
     */
    public char getFlag() { return flag; }
    
    /**
     *
     * @param flag
     */ 
    public void setFlag(char flag) { this.flag = flag; }
    
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
    
    /**
     * Get the payout interval from the configuration file
     * @return the payout interval from the configuration file
     */
    public int getPayoutInterval() { return payoutInterval; }
        
    /**
     * 
     * @param payoutInterval 
     */
    public void setPayoutInterval(int payoutInterval) { this.payoutInterval = payoutInterval; }
    
    /**
     * Get the payout amount from the configuration file
     * @return the payout amount from the configuration file
     */
    public int getPayoutAmount() { return payoutAmount; }
    
    /**
     * 
     * @param payoutAmount 
     */
    public void setPayoutAmount(int payoutAmount) { this.payoutAmount = payoutAmount; }
    
    /**
     * 
     * @return 
     */
    public boolean getCapsLockFilter() { return capsFilter; }
    
    /**
     * 
     * @param tof 
     */
    public void setCapsLockFilter(boolean tof) { capsFilter = tof; }
    
    /**
     * Check whether links are to be allowed or not.
     * @return True if links are disabled, false if not.
     */
    public boolean getLinksFitler() { return linksFilter; }
    
    /**
     * Set the status of links
     * @param tof True if links are disabled, false if not.
     */
    public void setLinksFilter(boolean tof) { linksFilter = tof; }
    
    /**
     * 
     * @return 
     */
    public boolean getGreetingStatus() { return greeting; }
    
    /**
     * 
     * @param tof 
     */
    public void setGreetingStatus(boolean tof) { greeting = tof; }
    
    /**
     * 
     * @return 
     */
    public boolean getBannedWordsFilter() { return bannedWords; }
    
    /**
     * 
     * @param tof 
     */
    public void setBannedWordsStatus(boolean tof) { bannedWords = tof; }
    
    /**
     * 
     * @return 
     */
    public boolean getEmoteGlobalFilter() { return emoteGlobalFilter; }
    
    /**
     * 
     * @param tof 
     */
    public void setEmoteFilter(boolean tof) { emoteGlobalFilter = tof; } 
    
    /**
     * 
     * @return 
     */
    public double getCapsTolerance() { return capsTolerance; }
    
    /**
     * 
     * @param capsTolerance 
     */
    public void setCapsTolernace(double capsTolerance) { this.capsTolerance = capsTolerance; }
    
    /**
     * 
     * @return 
     */
    public int getCapsMinLength() { return capsMinLength; }
    
    /**
     * 
     * @param minLength 
     */
    public void setCapsMinLength(int minLength) { capsMinLength = minLength; }
    
    /**
     * 
     * @return 
     */
    public int getEmoteTolerance() { return emotesTolerance; }
    
    /**
     * 
     * @param tolernace 
     */
    public void setEmoteTolerance(int tolernace) { emotesTolerance = tolernace; }
    
    /**
     * 
     * @return 
     */
    public boolean getSymbolFilter() { return symbolFilter; }
    
    /**
     * 
     * @param tof 
     */
    public void setSymbolFilter(boolean tof) { symbolFilter = tof; }   
    /**
     * 
     * @return 
     */
    public int getSymbolTolerance() { return symbolTolerance; }
    
    /**
     * 
     * @param symbolTolerance
     */
    public void setSymbolTolerance(int symbolTolerance) { this.symbolTolerance = symbolTolerance; }
    
	/**
	 *
	 * @return
	 */
    public boolean getEmoteSubFilter() { return emoteSubFilter; }
    
	/**
	 * @param tof
	 */
    public void setEmoteSubFilter(boolean tof) { emoteSubFilter = tof; }
}
