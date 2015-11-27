package lt.markmerkk.jira.interfaces;

/**
 * Created by mariusmerkevicius on 11/27/15.
 * Represents personal user data
 */
public interface ICredentials {
  /**
   * Provides user username
   * @return
   */
  String username();

  /**
   * Provides user password
   * @return
   */
  String password();

  /**
   * Provides server url
   * @return
   */
  String url();

  /**
   * Returns if user credentials are valid
   * @return
   */
  boolean isValid();

}
