package lt.markmerkk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 4/3/16.
 * Responsible for providing translations.
 *
 * Translations works as key/value from simple properties file.
 * @deprecated please use {@link Strings}
 */
@Deprecated
public class Translation {
  public static final Logger logger = LoggerFactory.getLogger(Translation.class);
  public static final String UNTRANSLATED = "untranslated";

  private static Translation sInstance;
  private final Properties translations;

  Translation() {
    // We do this in constructor for if anything is wrong, we break everything.
    translations = initTranslations();
  }

  public static Translation getInstance() {
    if (sInstance == null)
      sInstance = new Translation();
    return sInstance;
  }

  public synchronized String getString(String key) {
    if (key == null) return UNTRANSLATED;
    if (!translations.containsKey(key))
      return String.format("%s_%s", UNTRANSLATED, key);
    return translations.getProperty(key);
  }

  //region Convenience

  Properties initTranslations() {
    Properties translations;
    try {
      translations = new Properties();
      InputStream resourceAsStream = getClass().getResourceAsStream("/translations_en.properties");
      translations.load(resourceAsStream);
    } catch (IOException e) {
      logger.error("Error initializing translations!", e);
      throw new IllegalArgumentException("Error getting translations!");
    }
    return translations;
  }

  //endregion

}
