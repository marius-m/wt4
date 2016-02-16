package lt.markmerkk;

import com.google.common.base.Strings;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Responsible for synchronizing all the work logs from jira
 */
public class JiraSearchJQL implements Observable.OnSubscribe<Issue.SearchResult> {
  private static final Logger logger = LoggerFactory.getLogger(JiraSearchJQL.class);
  public final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  public static final String DEFAULT_JQL_WORKLOG_TEMPLATE = "key in workedIssues(\"%s\", \"%s\", \"%s\")";
  public static final String DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved)) AND (assignee = currentUser() OR reporter = currentUser())";

  JiraClient client;
  String jql;
  String searchFields;

  public JiraSearchJQL(JiraClient client, String jql, String searchFields) {
    this.client = client;
    this.jql = jql;
    this.searchFields = searchFields;
    if (Strings.isNullOrEmpty(searchFields))
      this.searchFields = "*all";
  }

  @Override
  public void call(Subscriber<? super Issue.SearchResult> subscriber) {
    try {
      if (client == null)
        throw new IllegalArgumentException("client == null");
      if (jql == null)
        throw new IllegalArgumentException("jql == null");
      logger.info("Doing search: "+jql);
      int batchCurrent = 0;
      int batchSize = 50;
      int batchTotal = 0;
      do {
        if (subscriber.isUnsubscribed())
          break;

        Issue.SearchResult sr = client.searchIssues(jql, searchFields, batchSize, batchCurrent);
        logger.info(String.format("Found %d issues. Current page %d. Max page %d. Total %d. ",
            sr.issues.size(), sr.start, sr.max, sr.total));
        logger.info("Found issues " + sr.issues.size() + " that have been worked on.");
        subscriber.onNext(sr);

        batchCurrent += sr.max;
        batchTotal = sr.total;
      } while (batchCurrent < batchTotal);
      subscriber.onCompleted();
    } catch (JiraException e) {
      subscriber.onError(e);
    }
  }

  //region Convenience


  //endregion

}
