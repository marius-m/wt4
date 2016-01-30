package lt.markmerkk;

import java.util.ArrayList;
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

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Responsible for synchronizing all the work logs from jira
 */
public class JiraLogger2 implements Observable.OnSubscribe<Map<String, List<WorkLog>>> {
  private static final Logger logger = LoggerFactory.getLogger(JiraLogger2.class);
  public final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
  public static final String JQL_WORKLOG_TEMPLATE =
      "key in workedIssues(\"%s\", \"%s\", \"%s\")";

  JiraClient client;
  DateTime startSearchDate, endSearchDate;

  public JiraLogger2(JiraClient client, DateTime startSearchDate, DateTime endSearchDate) {
    this.client = client;
    this.startSearchDate = startSearchDate;
    this.endSearchDate = endSearchDate;
  }

  @Override
  public void call(Subscriber<? super Map<String, List<WorkLog>>> subscriber) {
    try {
      if (client == null)
        throw new IllegalArgumentException("client == null");
      if (startSearchDate == null)
        throw new IllegalArgumentException("startSearchDate == null");
      if (endSearchDate == null)
        throw new IllegalArgumentException("endSearchDate == null");
      startSearchDate = startSearchDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
      endSearchDate = endSearchDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);

      subscriber.onNext(doFetchWorklog(startSearchDate, endSearchDate, client));
      subscriber.onCompleted();
    } catch (JiraException e) {
      subscriber.onError(e);
    } catch (InterruptedException e) { }
  }

  //region Convenience

  /**
   * Core method to do a search through JIRA remote
   * @param startSearchDate provided start search date
   * @param endSearchDate provided end search date
   * @param jira provided jira client
   * @throws JiraException
   */
  Map<String, List<WorkLog>> doFetchWorklog(DateTime startSearchDate, DateTime endSearchDate, JiraClient jira) throws JiraException, InterruptedException {
    logger.info("Looking on worked issues through " + longFormat.print(startSearchDate)
        + " to "
        + longFormat.print(endSearchDate)
        + " for "
        + jira.getSelf());
    String jql = String.format(JQL_WORKLOG_TEMPLATE,
        dateFormat.print(startSearchDate.getMillis()),
        dateFormat.print(endSearchDate.getMillis()),
        jira.getSelf()
    );
    logger.debug("Running JQL "+jql);
    Issue.SearchResult sr = jira.searchIssues(jql);
    logger.info("Found issues " + sr.issues.size() + " that have been worked on: ");
    Map<String, List<WorkLog>> logs = new HashMap<>();
    for (Issue i : sr.issues) {
      Issue issue = jira.getIssue(i.getKey());
      List<WorkLog> filteredLogs = filterLogs(jira.getSelf(), startSearchDate, endSearchDate, issue.getAllWorkLogs());
      logs.put(i.getKey(), filteredLogs);
      logger.info("Found " + filteredLogs.size() + " logs that have been worked on " + i.getKey());
    }
    return logs;
  }

  /**
   * Filters and validates pulled worklogs using {@link #filterLog(String, DateTime, DateTime, WorkLog)}
   * @param user provided user for the worklog
   * @param startSearchDate provided start search date
   * @param endSearchDate provided end search date
   * @param workLogList provided list of worklogs
   */
  List<WorkLog> filterLogs(String user, DateTime startSearchDate, DateTime endSearchDate, List<WorkLog> workLogList) {
    ArrayList<WorkLog> filterLogs = new ArrayList<>();
    if (isEmpty(user)) return filterLogs;
    if (startSearchDate == null) return filterLogs;
    if (endSearchDate == null) return filterLogs;
    if (workLogList == null) return filterLogs;
    for (WorkLog workLog : workLogList) {
      WorkLog filterLog = filterLog(user, startSearchDate, endSearchDate, workLog);
      if (filterLog == null) continue;
      filterLogs.add(filterLog);
    }
    return filterLogs;
  }

  /**
   * Filters {@link WorkLog} and returns it. If worklog fails validation, null is returned.
   *
   * @param user provided user for the worklog
   * @param startSearchDate provided start search date
   * @param endSearchDate provided end search date
   * @param workLog worklog to check
   */
  WorkLog filterLog(String user, DateTime startSearchDate, DateTime endSearchDate, WorkLog workLog) {
    if (user == null) return null;
    if (startSearchDate == null) return null;
    if (endSearchDate == null) return null;
    if (workLog == null) return null;
    if (!workLog.getAuthor().getName().equals(user)) return null;
    if (startSearchDate.isAfter(workLog.getStarted().getTime())) return null;
    if (endSearchDate.isBefore(workLog.getStarted().getTime())) return null;
    return workLog;
  }

  //endregion

}
