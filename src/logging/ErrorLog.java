package logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * The ErrorLog class will be used to create an error log file for reporting
 * bugs.
 * @author Michael Young
 */
public final class ErrorLog {
    private static BufferedWriter bw = null;
    
    /**
     * Initiate error logging. Creates the output stream and keeps the file
     * open to continuously write errors.
     */
    public static void initErrorLog() {
        try {
            bw = new BufferedWriter(new FileWriter("error.log", true));
        }
        catch (IOException e) {
            //This should never happen, but it could so....
            Console.writeLine("Could not open error logging stream. Can not log"
                    + " error. Isn't that ironic?\n" + e.toString() + "\t" + 
                    Arrays.toString(e.getStackTrace()) + "\nReport the following"
                    + "stack trace.");
            bw = null;
        }
    }
    
    /**
     * Write to the log. This writes the line that it occurred at and the actual
     * exception that happened.
     * @param e The error being logged
     */
    public static synchronized void writeToLog(Exception e) {
        if (bw != null) {
            try {
                bw.append(e.toString() + "\tStack trace: " + Arrays.toString(e.getStackTrace()) + "\n");
                bw.flush();
            }
            catch (IOException ex) {
                Console.writeLine("Could not write " + ex.toString() + " to error logging. Much irony, such bad, wow.");
            }
        }
    }
    
    /**
     * Close error logging. If it can't be closed, report it (because we're
     * logging errors that's why.)
     */
    public static void closeErrorLog() {
        if (bw == null) return;
        try {
            bw.close();
        }
        catch (IOException ex) {
            Console.writeLine("Could not close error logging. Logging this error.");
            ErrorLog.writeToLog(ex);
        }
    }
}
