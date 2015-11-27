package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.entities.SuccessResponse;
import lt.markmerkk.jira.interfaces.IResponse;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class JiraWorkerLogin extends JiraWorker<Object> {

  public static final String LOGIN = "LOGIN";

  public JiraWorkerLogin() { }

  @Override IResponse executeRequest(JiraRestClient client) {
    SuccessResponse<User> userJiraResponse = new SuccessResponse<>(tag(), "Login success!",
        client.getUserClient().getUser(credentials.username()).claim());
    return userJiraResponse;
  }

  @Override public void populateInput(Object inputData) { }

  @Override public String tag() {
    return LOGIN;
  }

  @Override public String preExecuteMessage() {
    return "Checking login status...";
  }
}
