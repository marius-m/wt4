package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.entities.SuccessResponse;
import lt.markmerkk.jira.interfaces.IResponse;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerLogin extends JiraWorker<User> {

  public static final String LOGIN = "LOGIN";

  public JiraWorkerLogin() { }

  @Override protected IResponse executeRequest(JiraRestClient client) {
    SuccessResponse<User> userJiraResponse = new SuccessResponse<>(tag(), "Login success!",
        client.getUserClient().getUser(credentials.username()).claim());
    return userJiraResponse;
  }

  @Override public void populateInput(User inputData) {
  }

  @Override public String tag() {
    return LOGIN;
  }

  @Override public String preExecuteMessage() {
    return "Checking login status...";
  }

  @Override public String postExecuteMessage(User entity) {
    return "User: "+entity.getName()+" / "+entity.getEmailAddress()+" / "+entity.getDisplayName();
  }
}
