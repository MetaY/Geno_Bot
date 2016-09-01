package logging;

import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * The console class is used to create a list of all strings that are read from
 * IRC and write them to a console in the GUI.
 * 
 * @author Michael Young
 */
public final class Console {
    
    private static final LinkedList<String> LL = new LinkedList<>();
    
    /**
     * Read a line from the console.
     * @return The first element stored in the linked list, null if nothing is
     *         in the linked list.
     */
    public static String readLine() {
        synchronized (LL) {
            if (!LL.isEmpty()) {
                return LL.remove();
            }
            else {
                return null;
            }
        }
    }
    
    /**
     * Write a line to the console.
     * @param s The string being written to the console.
     */
    public static void writeLine(String s) {
        synchronized (LL) {
            if (s.charAt(s.length() - 1) != '\n') {
                s += '\n';
            }
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            timestamp = timestamp.split(" ")[1];
            timestamp = timestamp.substring(0, timestamp.indexOf('.')) + " ";
            LL.add(timestamp + s);
        }
    }
}