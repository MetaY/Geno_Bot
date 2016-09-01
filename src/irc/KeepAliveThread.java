package irc;
/**
 * This thread is responsible for keeping the IRC connection alive.
 * @author Michael Young
 */
public final class KeepAliveThread extends Thread {
    
    @Override
    public void run() {
        this.setName("Keep Alive Thread");
        while (IRC.isConnected()) {
            try {
                Thread.sleep(30000);
                IRC.sendRaw("PING 1245");
            }
            catch (InterruptedException e) { break; }
        }
    }
}
