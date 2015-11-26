package lt.markmerkk.jira;

import lt.markmerkk.utils.Utils;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents user credentials that are used connecting to jira
 */
public class Credentials {
  String username;
  String password;
  String url;

  public Credentials(String username, String password, String url) {
    this.username = username;
    this.password = password;
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getUrl() {
    return url;
  }

  /**
   * Validates user credentials
   * @return
   */
  boolean isUserValid() {
    if (Utils.isEmpty(username)) return false;
    if (Utils.isEmpty(password)) return false;
    if (Utils.isEmpty(url)) return false;
    return true;
  }

}
