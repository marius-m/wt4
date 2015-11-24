package lt.markmerkk.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by mariusmerkevicius on 11/24/15.
 * Stores string data
 */
public abstract class HashSettings extends BaseSettings {
  public static final String KEY_HOST = "hostname";
  public static final String KEY_NAME = "username";

  Map<String, String> keyValues;

  public HashSettings() {
    keyValues = new HashMap<>();
  }

  public String get(String key) {
    if (!keyValues.containsKey(key))
      return "";
    return keyValues.get(key);
  }

  public void set(String key, String value) {
    keyValues.put(key, value);
  }

  @Override void onLoad(Properties properties) {
    keyValues.clear();
    for (Object o : properties.keySet())
      if (o instanceof String)
        keyValues.put((String)o, properties.getProperty((String)o));
  }

  @Override void onSave(Properties properties) {
    for (String s : keyValues.keySet())
      properties.put(s, keyValues.get(s));
  }
}
