package logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    public static void writeToFile(String filePath, String toWrite){
        File file = new File(filePath);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(toWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
