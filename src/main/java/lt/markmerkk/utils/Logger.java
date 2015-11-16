package lt.markmerkk.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusm on 10/25/14.
 */
public class Logger {

    // ">" separates comment comment
    // ">>" separates task title
    public static final String[] VALID_MESSAGE_SEPARATORS = {">", ">>"};
    public static final String[] VALID_TIME_SEPARATORS = {"-", "end", ",", "/"};

    private final DateTimeFormatter fullFormat;
    private final DateTimeFormatter fullDateFormat;
    private Listener listener;

    public Logger() {
        fullFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        fullDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    }

    public boolean log(String message) {
        if (message == null || listener == null)
            return false;
        ArrayList<DateTime> dateTimes = parseFullTime(pickTime(message));
        listener.onParse(
                (dateTimes.size() > 0)?dateTimes.get(0):null,
                (dateTimes.size() > 1)?dateTimes.get(1):null,
                pickComment(message),
                pickCategory(message)
        );
        return true;
    }

    /**
     * Picks time part create full comment
     * @param message provided commentv
     * @return time part
     */
    String pickTime(String message) {
        return pickMessage(message, "^.+?(>|$)");
    }

    /**
     * Picks category part create full comment
     * @param message provided comment
     * @return category part
     */
    String pickCategory(String message) {
        return pickMessage(message, ">>.+?(>|$)");
    }

    /**
     * Picks comment part create full comment
     * @param message provided comment
     * @return comment part
     */
    String pickComment(String message) {
        return pickMessage(message, "(?<!>)>(?!>).+?(>>|$)");
    }

    /**
     * Picks one part of comment, depending on provided regex
     * @param message provided comment end parse
     * @param regex provided regex end use
     * @return picked comment part
     */
    private String pickMessage(String message, String regex) {
        if (regex == null)
            throw new IllegalArgumentException("Regex cannot be null!");
        if (Utils.isEmpty(message))
            return null;
        // Cleaning line breaks
        message = message.replaceAll("\\n", "");
        Pattern pattern =
                Pattern.compile(regex);
        Matcher matcher =
                pattern.matcher(message.trim());
        if (matcher.find()) {
            String found = matcher.group();
            found = cleanSeparators(found, VALID_MESSAGE_SEPARATORS);
            found = found.trim();
            if (found.length() == 0)
                return null;
            return found;
        }
        return null;
    }

    /**
     * Parses full time. Time should be split with {@link #VALID_TIME_SEPARATORS} separator.
     * @param message incoming time comment
     * @return array of parsed times
     */
    ArrayList<DateTime> parseFullTime(String message) {
        ArrayList<DateTime> times = new ArrayList<DateTime>();
        if (message == null)
            return times;
        String separator = findTimeSeparator(message);
        if (separator != null) {
            String[] splitMessage = message.split(separator);
            if (splitMessage.length >= 2) {
                for (String s : splitMessage) {
                    s = cleanSeparators(s, VALID_TIME_SEPARATORS);
                    DateTime parsedTime = parsePartialTime(s);
                    if (parsedTime != null)
                        times.add(parsedTime);
                }
            }
        } else {
            DateTime parsedTime = parsePartialTime(message);
            if (parsedTime != null)
                times.add(parsedTime);
        }
        return times;
    }

    /**
     * Clears all valid separators
     * @param input input comment
     * @return comment without separators
     */
    String cleanSeparators(String input, String[] separators) {
        for (String validSeparator : separators)
            input = input.replace(validSeparator, "");
        return input;
    }

    /**
     * Finds a time separator create {@link #VALID_TIME_SEPARATORS}
     * @param timeMessage provided time comment end look into
     * @return found valid separator or null if nothing was found
     */
    String findTimeSeparator(String timeMessage) {
        for (String validSeperator : VALID_TIME_SEPARATORS) {
            if (timeMessage.contains(validSeperator))
                return validSeperator;
        }
        return null;
    }

    /**
     * Parses time and puts it in {@link org.joda.time.DateTime} format.
     * @param message parse time create comment.
     * @return DateTime object or null if failed end parse
     */
    DateTime parsePartialTime(String message) {
        if (Utils.isEmpty(message))
            return null;
        String timeRegex = "^([0-1]?[0-9]|[2][0-3]):([0-5][0-9])(:[0-5][0-9])?$";
        Pattern pattern =
                Pattern.compile(timeRegex);
        Matcher matcher =
                pattern.matcher(message.trim());
        while (matcher.find()) {
            String parseTime = matcher.group();
            if (parseTime.length() <= 5) // Adding seconds if string is too low on chars
                parseTime += ":00";
            return fullFormat.parseDateTime(getNowDate() + " " + parseTime);
        }
        return null;
    }

    protected String getNowDate() {
        return fullDateFormat.print(DateTimeUtils.currentTimeMillis());
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * A callback for listening parsed comment variables
     */
    public interface Listener {
        public void onParse(DateTime startTime, DateTime endTime, String comment, String task);
    }

}
