package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.entities.JiraResponse;
import lt.markmerkk.jira.interfaces.IJiraResponse;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class JiraWorkerLogin extends JiraWorker {

  public JiraWorkerLogin(Credentials credentials) {
    super(credentials);
  }

  @Override IJiraResponse executeRequest(JiraRestClient client) {
    return new JiraResponse<User>(client.getUserClient().getUser(credentials.getUsername()).claim(), "Login success!");
  }
}
