package lt.markmerkk.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.scene.control.TextArea;
import lt.markmerkk.Const;
import lt.markmerkk.Main;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 * Created by mariusm on 10/27/14.
 */
public class Utils {

    /**
     * Checks if string is empty or null
     * @param string provided string to check
     * @return empty flag
     */
    public static boolean isEmpty(String string) {
        return !(string != null && string.length() > 0);
    }

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

    /**
     * Returns last logged output
     */
    public static String lastLog(String logPath) {
        StringBuilder output = new StringBuilder();
        try {
            int maxLines = 150;
            int lineCount = 0;
            File file = new File(logPath + "info_prod.log");
            ReversedLinesFileReader object = new ReversedLinesFileReader(file);
            while (lineCount < maxLines) {
                String line = object.readLine();
                //if (line == null) return "";
                output.insert(0, line + "\n");
                lineCount++;
            }
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }
        return output.toString();
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
