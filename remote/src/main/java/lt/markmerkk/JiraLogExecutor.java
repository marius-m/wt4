package lt.markmerkk;

import java.util.ArrayList;
import java.util.List;
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

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Responsible for synchronizing all the work logs from jira
 */
public class JiraLogExecutor extends BaseExecutor2 {
  private static final Logger logger = LoggerFactory.getLogger(JiraLogExecutor.class);
  public final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
  public static final String JQL_WORKLOG_TEMPLATE =
      "key in workedIssues(\"2016-01-14\", \"2016-01-14\", \"marius.m@ito.lt\")";

  /**
   * Runs jira worklog pull for the provided date
   */
  public void runner(String host, String user, String pass, DateTime startSearchDate, DateTime endSearchDate) {
    try {
      if (isEmpty(host)) throw new JiraException("Error in JIRA address! Please fill in hostname for JIRA!");
      if (isEmpty(user)) throw new JiraException("Username is empty! Please fill in username / password");
      if (isEmpty(pass)) throw new JiraException("Password is empty! Please fill in username / password");
      BasicCredentials creds = new BasicCredentials(user, pass);
      JiraClient jira = new JiraClient(host, creds);

      startSearchDate = startSearchDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
      endSearchDate = endSearchDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
      doSearchWorklog(startSearchDate, endSearchDate, jira);
    } catch (JiraException e) {
      logger.debug(e.getMessage());
    }
  }

  //region Convenience

  /**
   * Core method to do a search through JIRA remote
   * @param startSearchDate provided start search date
   * @param endSearchDate provided end search date
   * @param jira provided jira client
   * @throws JiraException
   */
  List<WorkLog> doSearchWorklog(DateTime startSearchDate, DateTime endSearchDate, JiraClient jira) throws JiraException {
    Issue.SearchResult sr = jira.searchIssues(
        String.format(JQL_WORKLOG_TEMPLATE,
            dateFormat.print(startSearchDate.getMillis()),
            dateFormat.print(endSearchDate.getMillis())
        )
    );
    logger.info("Looking on worked issues through " + longFormat.print(startSearchDate)
        + " to "
        + longFormat.print(endSearchDate)
        + " for "
        + jira.getSelf());
    logger.info("Found issues " + sr.issues.size() + " that have been worked on: ");
    List<WorkLog> logs = new ArrayList<>();
    for (Issue i : sr.issues) {
      Issue issue = jira.getIssue(i.getKey());
      List<WorkLog> filteredLogs = filterLogs(jira.getSelf(), startSearchDate, endSearchDate, issue.getAllWorkLogs());
      logs.addAll(filteredLogs);
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

//  public void asyncRunner() {
//    executeInBackground(this::runner);
//  }

  @Override
  protected void onCancel() { }

  @Override
  protected void onReady() { }

  @Override
  protected void onFinish() { }

  @Override
  protected void onLoadChange(boolean loading) { }
}
