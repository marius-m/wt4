package lt.markmerkk.utils;

/**
 * Created by mariusmerkevicius on 11/24/15.
 */
public class SimpleSettings extends HashSettings {

  private static final String NAME = "name";
  private static final String HOST = "host";

  public String getHost() {
    return get(HOST);
  }

  public String getName() {
    return get(NAME);
  }

  public void setHost(String host) {
    set(HOST, host);
  }

  public void setName(String name) {
    set(NAME, name);
  }
}
