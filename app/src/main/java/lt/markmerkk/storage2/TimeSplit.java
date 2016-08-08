package lt.markmerkk.storage2;

import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lt.markmerkk.utils.LogFormatters;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;

/**
 * Created by mariusmerkevicius on 2/9/16. Parses and removes first part of the string message from
 * the seperator
 */
public class TimeSplit {
  public static final String SEPERATOR = ">>";
  public static final String[] VALID_MESSAGE_SEPARATORS = {SEPERATOR};

  /**
   * Adds a stamp to the raw comment.
   * Will try to remove older stamp if found one.
   * @param rawComment
   * @return
   */
  public static String addStamp(long start, long end, String rawComment) {
    if (Strings.isNullOrEmpty(rawComment)) return null;
    rawComment = removeStamp(rawComment); // Will remove older comment if found one
    return String.format("%s - %s "+TimeSplit.SEPERATOR+" %s", LogFormatters.INSTANCE.getShortFormat().print(start),
        LogFormatters.INSTANCE.getShortFormat().print(end), rawComment);
  }

  /**
   * Removes stamp from the comment if there is a seperator.
   *
   * @param message provided comment
   * @return category part
   */
  public static String removeStamp(String message) {
    if (Strings.isNullOrEmpty(message)) return null;
    if (!message.contains(SEPERATOR)) return message.trim();
    return pickMessage(message, "(" + SEPERATOR + ").+$");
  }

  /**
   * Picks one part of messge, depending on provided regex
   *
   * @param message provided comment end parse
   * @param regex provided regex end use
   * @return picked comment part
   */
  static String pickMessage(String message, String regex) {
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
   * Clears all valid separators
   *
   * @param input input comment
   * @return comment without separators
   */
  static String cleanSeparators(String input, String[] separators) {
    for (String validSeparator : separators)
      input = input.replace(validSeparator, "");
    return input;
  }

}
