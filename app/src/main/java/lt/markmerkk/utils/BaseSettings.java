package lt.markmerkk.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mariusmerkevicius on 11/24/15.
 * Uses a persistent storage to save user info
 */
public abstract class BaseSettings {
  public static final String PROPERTIES_FILE = "usr.properties";

  /**
   * Loads properties from file system
   * @param properties input file to load properties into
   */
  abstract void onLoad(Properties properties);

  /**
   * Saves properties into file system
   * @param properties input properties
   */
  abstract void onSave(Properties properties);

  //region Core

  /**
   * Core method to load properties from local storage
   */
  public void load() {
    try {
      FileInputStream in = null;
      in = new FileInputStream(PROPERTIES_FILE);
      Properties props = new Properties();
      props.load(in);
      onLoad(props);
      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Core method to save properties to local storage
   */
  public void save() {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(PROPERTIES_FILE);
      Properties props = new Properties();
      onSave(props);
      props.store(out, null);
      out.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //endregion

}
