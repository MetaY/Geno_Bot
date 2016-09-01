package logging;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * As many classes require File IO, this class will be used to manage the writes
 * and reads of objects. The functions are static to avoid the need to create
 * an object for this, and streams are created as needed.
 * @author Michael Young
 */
public final class ObjectIO {
    
    /**
     * Read an object from a given file.
     * @param filePath the name of the file in the local directory.
     * @return the object in the file, null if the file cannot be read.
     */
    public static Object readObjectFromFile(String filePath) {
        Object o = null;
        try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)))){
            o = input.readObject();
            input.close();
        }
        catch (FileNotFoundException e) {
            Console.writeLine(filePath + " does not exist.");
        }
        catch (IOException | ClassNotFoundException e) {
            //Should just happen if the file doesn't exist.
            ErrorLog.writeToLog(e);
            Console.writeLine("Could not read " + filePath);
        }
        return o;
    }
    
    /**
     * Write an object to file.
     * @param filePath The name of the file in the local directory.
     * @param o The object being written to file.
     */
    public static void writeObjectToFile(String filePath, Object o) {
        try (ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))) {
            output.writeObject(o);
            output.flush();
            output.close();
        }
        catch (IOException e) {
            Console.writeLine("Could not write " + filePath);
            ErrorLog.writeToLog(e);
        }
    }
}
