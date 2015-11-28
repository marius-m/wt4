package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerLogin extends JiraWorker {

  public static final String LOGIN = "LOGIN";

  public JiraWorkerLogin() { }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    SuccessWorkerResult<User>
        userJiraResponse = new SuccessWorkerResult<>(tag(),
        client.getUserClient().getUser(credentials.username()).claim());
    return userJiraResponse;
  }

  @Override public void populateInput(IWorkerResult result) { }

  @Override public String tag() {
    return LOGIN;
  }

  @Override public String preExecuteMessage() {
    return "Checking login status...";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      if (!(result.entity() instanceof User)) return "Internal error: Result of wrong type!";
      User user = (User) result.entity();
      return "  Success: User: "+user.getName();
    }
    return "Unknown internal error!";
  }
}
