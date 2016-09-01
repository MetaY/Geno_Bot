package data;

import logging.ObjectIO;
import irc.IRC;
import logging.ErrorLog;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class keeps track of all the timer based commands a streamer creates.
 * Commands will be ran on a given delay in minutes, and a thread will keep
 * track of of when a message is sent via IRC. Only the commands array list
 * will be written to file, as it's not worth writing the threads to file.
 * It doesn't take much to create the threads each time, and it uses less space
 * to just save the commands versus saving the threads.
 * @author Michael Young
 */
public class TimerCommands implements Serializable {
    private final ArrayList<TimedCommand> commands;
    private final ArrayList<TimerThread> THREADS;
    private boolean isRunning;
    
    /**
     * Create the Timer Command database.  As there shouldn't be many timer
     * commands, linear time to access them should be acceptable, so array lists
     * are used in favor of space.
     */
    public TimerCommands() {
        commands = new ArrayList<>();
        THREADS = new ArrayList<>();
        isRunning = false;
    }
    /**
     * Add a new timer command. The state of the timer threads will be set to
     * true.
     * 
     * @param com The name of the command
     * @param m The message to be displayed
     * @param d The delay in minutes of the command
     * @return whether to timer was either updated or created.
     */
    public synchronized String addCommand(String com, String m, int d) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).command.equals(com)) {
                commands.get(i).message = m;
                commands.get(i).delay = d;
                THREADS.add(new TimerThread(commands.get(i)));
                THREADS.get(i).start();
                isRunning = true;
                return "Timer already exists. Updating timer.";
            }
        }
        commands.add(new TimedCommand(com, m, d));
        
        return "Timer added successfully";
    }
    
    /**
     * Removes a command from the timer database. The state of the timers
     * will be set to false only if there are no timers running.
     * @param com The name of the command.
     * @return whether the command was removed or if it didn't exist.
     */
    public synchronized String removeCommand(String com) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).command.equals(com)) {
                THREADS.get(i).interrupt();
                THREADS.remove(i);
                commands.remove(i);
                if (THREADS.isEmpty()) {
                    isRunning = false;
                }
                return "Timer removed successfully.";
            }
        }
        return "Timer does not exist.";
    }
    
    /**
     * Start all the threads.
     */
    public synchronized void startCommands() {
        if (isRunning) {
            stopCommands();
        }
        for (int i = 0; i < commands.size(); i++) {
            THREADS.add(new TimerThread(commands.get(i)));
            THREADS.get(i).start();
        }
        isRunning = true;
    }
    
    /**
     * Stop all commands and clear the command threads. There is no reason to
     * waste space if they aren't running.
     */
    public synchronized void stopCommands() {
        for (int i = 0; i < THREADS.size(); i++) {
            //We'll interrupt the thread(s), forcing the catch statement
            THREADS.get(i).interrupt();
        }
        THREADS.clear();
        isRunning = false;
    }
    
    /**
     * Check the state of the timers
     * @return True if there is a timer running, false if not.
     */
    public boolean isRunning() { return isRunning; }
    
    /**
     * Get a list of all timers.
     * @return A string with all timer names.
     */
    public synchronized String timers() {
        String s = "";
        for (int i = 0; i < commands.size() - 1; i++) {
            s += commands.get(i).command + ", ";
        }
        s += commands.get(commands.size() - 1).command;
        return s;
    }
    
    /**
     * The TimedCommands class stores all the information necessary to create
     * a timer. The requirements are the name of the command, the message to be
     * sent to IRC, and the delay in minutes between sending the message.
     */
    private class TimedCommand implements Serializable {
        private String command;
        private String message;
        private int delay;

        /**
         * Create a new command that is to be based off a timer.
         * @param message
         * @param delay the time between the message being sent, in minutes
         */
        public TimedCommand(String command, String message, int delay) {
            this.command = command;
            this.message = message;
            this.delay = delay * 60000;
        }
    }
    
    /**
     * The TimerThread class will be what is responsible for actually sending
     * the messages.  It only needs a TimedCommand object and will reference
     * it's data to run, and the thread will be recreated on each run of the
     * program.
     */
    private class TimerThread extends Thread {
        private final TimedCommand TC;
        
        /**
         * Create a new thread to run commands.
         * @param tc The TimedCommand object that will be referenced.
         */
        public TimerThread(TimedCommand tc) { this.TC = tc; }
        
        /**
         * Run function for the thread. This will be called by start()
         */
        @Override
        public void run() {
            this.setName(TC.command);
            while (!isInterrupted()) {
                try {
                    Thread.sleep(TC.delay);
                    IRC.sendMessage(TC.message);
                }
                catch (InterruptedException e) { break; }
            }
        }
    }
}
