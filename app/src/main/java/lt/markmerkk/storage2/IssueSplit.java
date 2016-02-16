package lt.markmerkk.storage2;

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lt.markmerkk.utils.Utils;

/**
 * Created by mariusmerkevicius on 2/16/16.
 * Responsible for splitting search phrase for more accurate results
 * traversing local database
 */
public class IssueSplit {
  public static final String SEPERATOR = ":";
  public static final String[] VALID_MESSAGE_SEPARATORS = {SEPERATOR};

  public static final String KEY_KEY = "KEY_KEY";
  public static final String KEY_REGEX = "^.+"+"("+SEPERATOR+")";
  public static final String DESCRIPTION_KEY = "DESCRIPTION_KEY";
  public static final String DESCRIPTION_REGEX = "(" + SEPERATOR + ").+$";

  public Map<String, String> split(String inputPhrase) {
    HashMap<String, String> map = new HashMap<>();
    map.put(DESCRIPTION_KEY, pickPart(inputPhrase, DESCRIPTION_REGEX));
    map.put(KEY_KEY, pickPart(inputPhrase, KEY_REGEX));
    return map;
  }

  //region Convenience

  /**
   * Will pick part of message depending on regex
   * @param inputPhrase
   * @return
   */
  String pickPart(String inputPhrase, String regex) {
    if (Strings.isNullOrEmpty(inputPhrase)) return "";
    String description = pickMessage(inputPhrase, regex);
    if (description == null)
      description = inputPhrase;
    return description.trim();
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

  //endregion

}
