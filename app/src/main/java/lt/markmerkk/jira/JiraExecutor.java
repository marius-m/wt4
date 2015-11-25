package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import javafx.application.Platform;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class JiraExecutor extends TaskExecutor<IRemoteObject> implements IRemote {

  String username;
  String password;
  String url;

  JiraRestClient client;

  JiraListener listener;

  public JiraExecutor(JiraListener listener) {
    this.listener = listener;
  }

  //region World events

  @Override public void onStart() {
    super.onStart();
  }

  @Override public void onStop() {
    close();
    super.onStop();
  }

  //endregion

  //region Callback

  @Override protected void onResult(IRemoteObject result) {
    Platform.runLater(() -> {
      if (listener == null) return;
      if (result.error() != null) {
        listener.onError(result.error());
        return;
      }
      listener.onLoginSuccess();
    });
  }

  @Override protected void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      if (listener == null) return;
      listener.onLoadChange(loading);
    });
  }

  //endregion

  /**
   * Checks login validation
   * @param url provided url
   * @param username provided username
   * @param password provided password
   */
  public void checkIsLoginValid(String url, String username, String password) {
    executeInBackground(() -> {
      try {
        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        client = factory.createWithBasicHttpAuthentication(new URI(url), username, password);
        return new JiraObject<User>(client.getUserClient().getUser(username).claim());
      } catch (URISyntaxException e) {
        System.out.println(e.getMessage());
        return new JiraObject("Error: "+e.getMessage());
      } catch (RestClientException e) {
        System.out.println(e.getMessage());
        return new JiraObject("Error: "+e.getMessage());
      } catch (IllegalArgumentException e) {
        return new JiraObject("Error: "+e.getMessage());
      } finally {
        close();
      }
    });
  }

  //region Convenience

  /**
   * Closes any jira connection
   */
  private void close() {
    try {
      if (client != null)
        client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //endregion

}
