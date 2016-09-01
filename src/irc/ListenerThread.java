package irc;

import java.io.BufferedReader;
import java.io.IOException;
import logging.Console;
import logging.ErrorLog;

/**
 *
 * @author Michael Young
 */
public class ListenerThread extends Thread {
    
    private final BufferedReader IN;
    
    public ListenerThread(BufferedReader in) {
        IN = in;
        setName("Listener Thread");
    }
    
    @Override
    public void run() {
        while (IRC.isConnected()) {
            try {
                String s = IN.readLine();
                System.out.println(s);
                IRC.parseMessage(s);
            }
            catch (IOException e) {
                //Error logging needs to be put here, figure it out
                Console.writeLine("Disconnected from IRC.");
            }
        }
    }
}
