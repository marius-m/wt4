package lt.markmerkk.utils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.JiraSearchJQL;
import lt.markmerkk.listeners.WorldEvents;

/**
 * Created by mariusmerkevicius on 12/21/15.
 * Controller for holding persistent data
 */
public class UserSettings implements WorldEvents {
  public static final String HOST = "HOST";
  public static final String USER = "USER";
  public static final String PASS = "PASS";
  public static final String VERSION = "VERSION";
  public static final String ISSUE_JQL = "ISSUE_JQL";

  AdvHashSettings settings;

  // Used settings
  String host;
  String username;
  String password;
  String issueJql;
  int version = -1;

  public UserSettings() {
    settings = new AdvHashSettings();
  }

  //region Getters / Setters


  public String getIssueJql() {
    if (issueJql == null) return JiraSearchJQL.DEFAULT_JQL_USER_ISSUES;
    return issueJql;
  }

  public void setIssueJql(String issueJql) {
    this.issueJql = issueJql;
    settings.save();
  }

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

  public void setCustom(String key, String value) {
    settings.set(key, value);
    settings.save();
  }

  public String getCustom(String key) {
    return settings.get(key);
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
    settings.save();
  }

  //endregion

  //region World events

  @PostConstruct
  @Override public void onStart() {
    settings.load();
    host = settings.get(HOST);
    username = settings.get(USER);
    password = settings.get(PASS);
    String versionString = settings.get(VERSION);
    version = (versionString == null) ? -1 : Integer.parseInt(versionString);
    issueJql = settings.get(ISSUE_JQL);
  }

  @PreDestroy
  @Override public void onStop() {
    settings.set(HOST, getHost());
    settings.set(USER, getUsername());
    settings.set(PASS, getPassword());
    settings.set(VERSION, String.valueOf(version));
    settings.set(ISSUE_JQL, getIssueJql());
    settings.save();
  }

  //endregion
}
