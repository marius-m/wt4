package lt.markmerkk;

import java.util.List;
import java.util.Map;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import rx.Observable;
import rx.Subscriber;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by mariusmerkevicius on 1/29/16.
 * Responsible for validating credengials and connecting
 * {@link JiraClient}
 */
public class JiraConnector implements Observable.OnSubscribe<JiraClient> {

  String hostname, username, password;

  public JiraConnector(String hostname, String username, String password) {
    this.hostname = hostname;
    this.username = username;
    this.password = password;
  }

  @Override
  public void call(Subscriber<? super JiraClient> subscriber) {
    try {
      if (isEmpty(hostname)) throw new JiraException("Error in JIRA address! Please fill in hostname for JIRA!");
      if (isEmpty(username)) throw new JiraException("Username is empty! Please fill in username / password");
      if (isEmpty(password)) throw new JiraException("Password is empty! Please fill in username / password");

      BasicCredentials creds = new BasicCredentials(username, password);
      JiraClient jira = new JiraClient(hostname, creds);

      subscriber.onNext(jira);
      subscriber.onCompleted();
    } catch (JiraException e) {
      subscriber.onError(e);
    }
  }
}
