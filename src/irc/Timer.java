package irc;

/**
 * A timer that will determine how much time has elapsed since instantiation.
 * This class uses System.nanoTime() calls
 * There may be minor revisions of this class, but doubtful.
 * @author Michael Young
 */
public class Timer {
    
    
    /**
     * The number of nanoseconds per hour.
     */
    public static final long NANO_PER_HOUR = 3600000000000L;
    
    /**
     * The number of nanoseconds per minute.
     */
    public static final long NANO_PER_MINUTE = 60000000000L;
    
    /**
     * The number of nanoseconds per second.
     */
    public static final long NANO_PER_SECOND = 1000000000L;
    
    private long start;
    
    /**
     * Constructs a timer and gives it the initial System.nanoTime() call.
     */
    public Timer() { start = System.nanoTime(); }
    
    /**
     * Reset the timer to the current System.nanoTime() call.
     */
    public void reset() { start = System.nanoTime(); }
    
    /**
     * Get the duration since instantiation.
     * @return The time since instantiation in nanoseconds.
     */
    private long getDuration() { return System.nanoTime() - start; }
    
    /**
     * Create a string of how long has passed.
     * @return A string in in hh:mm:ss format. The string can have an overflow
     * error if System.nanoTime() becomes negative. It shouldn't happen, but it
     * is to be noted.
     */
    @Override
    public String toString() {
        long hours, minutes;
        double seconds;
        long duration = getDuration();
        hours = duration / NANO_PER_HOUR;
        duration %= NANO_PER_HOUR;
        minutes = duration / NANO_PER_MINUTE;
        duration %= NANO_PER_MINUTE;
        seconds = duration / (double) NANO_PER_SECOND;
        return String.format("%d:%02d:%02d", hours, minutes, Math.round(seconds));
    }
}
