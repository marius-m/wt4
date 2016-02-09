package lt.markmerkk.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lt.markmerkk.Main;

/**
 * Created by mariusmerkevicius on 11/24/15.
 * Stores string data
 */
public class AdvHashSettings extends BaseSettings {
  public static final String PROPERTIES_PATH = "usr.properties";
  Map<String, String> keyValues;

  public AdvHashSettings() {
    keyValues = new HashMap<>();
  }

  public String get(String key) {
    if (!keyValues.containsKey(key))
      return null;
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
      if (o instanceof String) {
        String property = properties.getProperty((String) o);
        if (Utils.isEmpty(property)) continue;
        byte[] decodeBytes = Base64.decode(property, 0);
        String decodeValue = new String(decodeBytes);
        keyValues.put((String)o, decodeValue);
      }
  }

  @Override void onSave(Properties properties) {
    for (String s : keyValues.keySet()) {
      String value = keyValues.get(s);
      properties.put(s, new String(Base64.encode(value.getBytes(), 0)).trim());
    }
  }
}
