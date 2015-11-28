package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerLogin extends JiraWorker {

  public static final String LOGIN = "LOGIN";

  public JiraWorkerLogin() { }

  @Override protected IWorkerResult executeRequest(JiraRestClient client) {
    SuccessWorkerResult<User>
        userJiraResponse = new SuccessWorkerResult<>(tag(),
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

  @Override public String postExecuteMessage(Object entity) {
    if (entity == null) return null;
    try {
      User user = (User) entity;
      return "User: "+user.getName()+" / "+user.getEmailAddress()+" / "+user.getDisplayName();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
