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
     * Formats duration time into pretty string format
     * @param durationMillis provided duration to format
     * @return formatted duration
     */
    public static String formatDuration(long durationMillis) {
        if (durationMillis < 1000)
            return "0s";
        StringBuilder builder = new StringBuilder();
        PeriodType type = PeriodType.forFields(new DurationFieldType[]{
            DurationFieldType.hours(),
            DurationFieldType.minutes(),
            DurationFieldType.seconds()
        });

        Period period = new Period(durationMillis, type);
        if (period.getDays() != 0)
            builder.append(period.getDays()).append("d").append(" ");
        if (period.getHours() != 0)
            builder.append(period.getHours()).append("h").append(" ");
        if (period.getMinutes() != 0)
            builder.append(period.getMinutes()).append("m").append(" ");
        if (period.getSeconds() != 0)
            builder.append(period.getSeconds()).append("s").append(" ");
        if ((builder.length() > 0) && builder.charAt(builder.length()-1) == " ".charAt(0))
            builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    /**
     * Formats duration time into pretty and short string format
     * @param durationMillis provided duration to format
     * @return formatted duration
     */
    // fixme : needs tests, as this code was copied from earlier project
    public static String formatShortDuration(long durationMillis) {
        if (durationMillis < (1000*60))
            return "0m";
        StringBuilder builder = new StringBuilder();
        PeriodType type = PeriodType.forFields(new DurationFieldType[]{
            DurationFieldType.hours(),
            DurationFieldType.minutes()
        });

        Period period = new Period(durationMillis, type);
        if (period.getDays() != 0)
            builder.append(period.getDays()).append("d").append(" ");
        if (period.getHours() != 0)
            builder.append(period.getHours()).append("h").append(" ");
        if (period.getMinutes() != 0)
            builder.append(period.getMinutes()).append("m").append(" ");
        if ((builder.length() > 0) && builder.charAt(builder.length()-1) == " ".charAt(0))
            builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    public static final String SEPERATOR = "-";

    /**
     * Inspects id for a valid type
     * @param message
     */
    public static String validateTaskTitle(String message) {
        if (Utils.isEmpty(message))
            return "";
        message = message.replaceAll("\\n", "");
        Pattern pattern =
            Pattern.compile("[a-zA-Z]+(-)?[0-9]+");
        Matcher matcher =
            pattern.matcher(message.trim());
        if (matcher.find()) {
            String found = matcher.group();
            found = found.toUpperCase();
            found = found.trim();
            if (!found.contains(SEPERATOR))
                found = insertTaskSeperator(found);
            if (found.length() == 0)
                return null;
            return found;
        }
        return "";
    }

    /**
     * Insers a missing seperator if it is missing.
     * @param message message that should be altered
     * @return altered message with seperator attached to its proper spot.
     */
    public static String insertTaskSeperator(String message) {
        if (message == null)
            return null;
        Pattern pattern =
            Pattern.compile("[a-zA-Z]+[^0-9]");
        Matcher matcher =
            pattern.matcher(message.trim());
        if (matcher.find()) {
            message = message.substring(0, matcher.end())
                +SEPERATOR
                +message.substring(matcher.end(), message.length());
        }
        return message;
    }

    /**
     * Splits task title into
     */
    public static String splitTaskTitle(String message) {
        if (Utils.isEmpty(message)) return null;
        message = message.replaceAll("\\n", "");
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(message.trim());
        if (matcher.find()) {
            String found = matcher.group();
            found = found.toUpperCase();
            found = found.trim();
            if (found.length() == 0) return null;
            return found;
        }
        return null;
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
    public static String lastLog() {
        StringBuilder output = new StringBuilder();
        try {
            int maxLines = 150;
            int lineCount = 0;
            File file = new File(Main.CFG_PATH + "info.log");
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
