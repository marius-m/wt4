package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.utils.Utils;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents user credentials that are used connecting to jira
 */
public class Credentials implements ICredentials {
  String username;
  String password;
  String url;

  public Credentials(String username, String password, String url) {
    this.username = username;
    this.password = password;
    this.url = url;
  }

  @Override public String username() {
    return username;
  }

  @Override public String password() {
    return password;
  }

  @Override public String url() {
    return url;
  }

  @Override public boolean isValid() {
    if (Utils.isEmpty(username)) return false;
    if (Utils.isEmpty(password)) return false;
    if (Utils.isEmpty(url)) return false;
    return true;
  }
}
