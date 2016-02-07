package lt.markmerkk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Responsible for synchronizing all the work logs from jira
 */
public class JiraSearchJQL implements Observable.OnSubscribe<Issue.SearchResult> {
  private static final Logger logger = LoggerFactory.getLogger(JiraSearchJQL.class);
  public final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  public static final String JQL_WORKLOG_TEMPLATE = "key in workedIssues(\"%s\", \"%s\", \"%s\")";

  JiraClient client;
  String jql;

  public JiraSearchJQL(JiraClient client, String jql) {
    this.client = client;
    this.jql = jql;
  }

  @Override
  public void call(Subscriber<? super Issue.SearchResult> subscriber) {
    try {
      if (client == null)
        throw new IllegalArgumentException("client == null");
      if (jql == null)
        throw new IllegalArgumentException("jql == null");
      logger.info("Doing search: "+jql);
      Issue.SearchResult sr = client.searchIssues(jql);
      logger.info("Found issues " + sr.issues.size() + " that have been worked on.");
      subscriber.onNext(sr);
      subscriber.onCompleted();
    } catch (JiraException e) {
      subscriber.onError(e);
    }
  }

  //region Convenience


  //endregion

}
