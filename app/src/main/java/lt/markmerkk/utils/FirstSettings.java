package lt.markmerkk.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import lt.markmerkk.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/24/15.
 * First time launch settings
 */
public class FirstSettings extends BaseSettings {
  Logger logger = LoggerFactory.getLogger(FirstSettings.class);
  public static final String PROPERTIES_FILE = "first.properties";

  boolean firstLaunch = true;

  //region Core

  public boolean isFirst() {
    return firstLaunch;
  }

  @Override
  String propertyPath() {
    return PROPERTIES_FILE;
  }

  @Override
  void onLoad(Properties properties) {
    String firstProperty = properties.getProperty("first", "true");
    firstLaunch = !"false".equals(firstProperty);
  }

  @Override
  void onSave(Properties properties) {
    properties.put("first", "false");
  }

  //endregion

}
