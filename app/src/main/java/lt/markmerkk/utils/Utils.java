package lt.markmerkk.utils;

import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by mariusm on 10/27/14.
 */
public class Utils {

    /**
     * Checks if array is empty or null
     * @param array provided array to check
     * @return empty flag
     */
    public static boolean isArrayEmpty(ArrayList<String> array) {
        if (array == null)
            return true;
        if (array.size() == 0)
            return true;
        return false;
    }

    /**
     * Fills provided text area with an old log
     * @param textArea
     */
    public static void fillAllLog(TextArea textArea) {
        if (textArea == null) return;
        textArea.clear();
        try (Stream<String> stream = Files.lines(Paths.get("checkLog.log"), Charset.defaultCharset())) {
            stream.forEach(textArea::appendText);
        } catch (IOException ex) { }
    }

    public static String lastLog(String logPath, int lines) {
        RandomAccessFile fileHandler = null;
        try {
            File file = new File(logPath + "info_prod.log");
            fileHandler = new RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;
            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();
                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append((char) readByte);
            }
            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (fileHandler != null)
                try {
                    fileHandler.close();
                } catch (IOException e) { }
        }
    }

  /**
   * Recursively cleans out directory
   * @param file
   * @return
   */
  public static boolean delete(File file) {
        File[] flist = null;
        if (file == null)
            return false;
        if (file.isFile())
            return file.delete();
        if (!file.isDirectory())
            return false;
        flist = file.listFiles();
        if (flist != null && flist.length > 0) {
            for (File f : flist) {
                if (!delete(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

}
