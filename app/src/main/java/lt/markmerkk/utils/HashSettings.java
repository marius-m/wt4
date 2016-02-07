package lt.markmerkk.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lt.markmerkk.Main;

/**
 * Created by mariusmerkevicius on 11/24/15.
 * Stores string data
 */
public class HashSettings extends BaseSettings {
  public static final String PROPERTIES_PATH = "simple.properties";
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
    if (key == null || value == null) return;
    keyValues.put(key, value);
  }

  @Override
  String propertyPath() {
    return Main.CFG_PATH + PROPERTIES_PATH;
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
