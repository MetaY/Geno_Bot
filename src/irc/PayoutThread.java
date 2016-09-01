package irc;

import data.UserDatabase;
import java.util.List;

/**
 * This thread is responsible for paying users the designated number of points
 * in the given time interval.
 * @author Michael Young
 */
public class PayoutThread extends Thread {
    
    private int interval;
    private int payout;
    private final UserDatabase DB;
    private final List<String> CURRENT_VIEWERS;
    
    public PayoutThread(int interval, int payout, UserDatabase db, List<String> currentViewers) {
        this.interval = interval;
        this.payout = payout;
        this.DB = db;
        this.CURRENT_VIEWERS = currentViewers;
    }
    
    @Override
    public void run() {
        this.setName("Points Thread");
        if (interval == 0 || payout == 0) return;   //No point for this thread to run
        while (IRC.isConnected()) {
            try {
                Thread.sleep(interval);
            }
            catch(InterruptedException e) {
                break;
            }
            synchronized (CURRENT_VIEWERS) {
                for (String CURRENT_VIEWERS1 : CURRENT_VIEWERS) {
                    DB.changePoints(CURRENT_VIEWERS1, payout);
                }
            }
            DB.writeDatabase();
        }
    }
    
    public void setInterval(int interval) { this.interval = interval; }
    
    public void setPayout(int payout) { this.payout = payout; }
}
