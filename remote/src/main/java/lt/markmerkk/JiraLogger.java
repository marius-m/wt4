package lt.markmerkk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by mariusmerkevicius on 1/27/16.
 * A RX implementation of jira logging.
 */
public class JiraLogger {
  private static final Logger logger = LoggerFactory.getLogger(JiraLogExecutor.class);
  public final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
  public static final String JQL_WORKLOG_TEMPLATE =
      "key in workedIssues(\"%s\", \"%s\", \"%s\")";

  IRemoteListener remoteListener;
  IRemoteLoadListener remoteLoadListener;

  public JiraLogger(IRemoteListener remoteListener, IRemoteLoadListener remoteLoadListener) {
    if (remoteListener == null)
      throw new IllegalArgumentException("remoteListener == null");
    if (remoteLoadListener == null)
      throw new IllegalArgumentException("remoteLoadListener == null");
    this.remoteListener = remoteListener;
    this.remoteLoadListener = remoteLoadListener;
  }

  public void fetch(String host, String user, String pass, DateTime startSearchDate, DateTime endSearchDate) throws
      JiraException {
    if (isEmpty(host)) throw new JiraException("Error in JIRA address! Please fill in hostname for JIRA!");
    if (isEmpty(user)) throw new JiraException("Username is empty! Please fill in username / password");
    if (isEmpty(pass)) throw new JiraException("Password is empty! Please fill in username / password");
    BasicCredentials creds = new BasicCredentials(user, pass);
    final JiraClient jira = new JiraClient(host, creds);

    final String jql = String.format(JQL_WORKLOG_TEMPLATE,
        dateFormat.print(startSearchDate.getMillis()),
        dateFormat.print(endSearchDate.getMillis()),
        jira.getSelf()
    );

    startSearchDate = startSearchDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
    endSearchDate = endSearchDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);

    Observable<Issue.SearchResult> searchResultObservable = searchResult(jira, jql);

  }

  //region Observables

  Observable<Issue.SearchResult> searchResult(JiraClient jira, String jql) throws JiraException {
    return Observable.just(jira.searchIssues(jql));
  }

  //endregion

}
