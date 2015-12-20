package lt.markmerkk.utils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.listeners.WorldEvents;

/**
 * Created by mariusmerkevicius on 12/21/15.
 */
public class UserSettings implements WorldEvents {
  public static final String HOST = "HOST";
  public static final String USER = "USER";
  public static final String PASS = "PASS";

  AdvHashSettings settings;

  // Used settings
  String host;
  String username;
  String password;

  public UserSettings() {
    settings = new AdvHashSettings();
  }

  //region Getters / Setters

  public String getHost() {
    if (host == null) return "";
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUsername() {
    if (username == null) return "";
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    if (password == null) return "";
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  //endregion

  //region World events

  @PostConstruct
  @Override public void onStart() {
    settings.load();
    host = settings.get(HOST);
    username = settings.get(USER);
    password = settings.get(PASS);
  }

  @PreDestroy
  @Override public void onStop() {
    settings.set(HOST, getHost());
    settings.set(USER, getUsername());
    settings.set(PASS, getPassword());
    settings.save();
  }

  //endregion
}
