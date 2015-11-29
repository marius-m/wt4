package lt.markmerkk;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInputBuilder;
import java.util.concurrent.TimeUnit;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import lt.markmerkk.utils.Utils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Responsible for merging / updating database {@link SimpleLog} entities
 * with the remote ones.
 *
 * This merger should push all local entities to the server:
 * 1. Entities that does not have a server id
 *
 */
public class PushNewMerger implements IMerger {
  // Max count of recursive push
  public static final int MAX_COUNT = 50;
  private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy/MM/dd");
  private final DateTime targetDate;

  IExecutor executor;
  JiraRestClient client;

  public PushNewMerger(IExecutor executor, JiraRestClient client, DateTime targetDate) {
    if (executor == null)
      throw new IllegalArgumentException("Cannot function without database!");
    if (client == null)
      throw new IllegalArgumentException("Cannot function without a Jira client!");
    if (targetDate == null)
      throw new IllegalArgumentException("Cannot function without a target date!");
    this.client = client;
    this.executor = executor;
    this.targetDate = targetDate;
  }

  @Override public String merge() {
    String statusLog = "";
    int count = 0;
    do {
      SimpleLog log = newRemoteLog(executor);
      if (log == null) break;
      // Probably need a validator for comment, task not to be null
      statusLog += "\n    Uploading new log: "+log+". ";
      if (Utils.isEmpty(log.getTask())) {
        updateLocalLog(new SimpleLogBuilder(log).buildWithError("Error getting issue!"));
        statusLog += "\n      Error: Issue not found!";
        return statusLog;
      }
      if (Utils.isEmpty(log.getComment())) {
        updateLocalLog(new SimpleLogBuilder(log).buildWithError("Error getting comment!"));
        statusLog += "\n      Error: Comment not found!";
        return statusLog;
      }

      statusLog += upload(log);
    } while (++count < MAX_COUNT);
    return statusLog;
  }

  // We use wrappers as convenience and dedicated jobs for other classes
  //region Convenience wrappers

  /**
   * Tries to upload a task to database
   * @param log
   * @return
   */
  String upload(SimpleLog log) {
    String statusLog = "";
    IssueRestClient issueClient = client.getIssueClient();
    Issue issue = issueClient.getIssue(log.getTask()).claim();
    if (issue == null) {
      updateLocalLog(new SimpleLogBuilder(log).buildWithError("Issue not found!"));
      statusLog += "\n      Error: Issue not found!";
      return statusLog;
    }

    WorklogInputBuilder worklogInputBuilder = new WorklogInputBuilder(issue.getSelf());
    final WorklogInput worklogInput = worklogInputBuilder
        .setIssueUri(issue.getSelf())
        .setAdjustEstimateAuto()
        .setComment(log.getComment())
        .setStartDate(new DateTime(log.getStart()))
        .setMinutesSpent((int)TimeUnit.MILLISECONDS.toMinutes(log.getDuration()))
        .build();
    issueClient.addWorklog(issue.getWorklogUri(), worklogInput).claim();
    deleteLocalLog(log);
    statusLog += "\n      Successfully uploaded!";
    return statusLog;
  }

  /**
   * Updates log into local database
   * @param log
   */
  void updateLocalLog(SimpleLog log) {
    executor.execute(new UpdateJob(SimpleLog.class, log));
  }

  /**
   * Updates log into local database
   * @param log
   */
  void deleteLocalLog(SimpleLog log) {
    executor.execute(new DeleteJob(SimpleLog.class, log));
  }

  /**
   * Convenience method to pull the log that needs to be uploaded for the remote server
   * @param executor database executor
   * @return
   */
  SimpleLog newRemoteLog(IExecutor executor) {
    // Returning all the jobs that are dirty and does not have a server
    QueryJob<SimpleLog> queryJob = new QueryJob<>(SimpleLog.class,
        () -> "(start > " + targetDate.getMillis()
            + " AND "
            + "end < " + targetDate.plusDays(1).getMillis()
            + " AND "
            + " dirty = 1"
            + " AND "
            + " id = 0"
            + " AND "
            + " error = 0"
            + ")");
    executor.execute(queryJob);
    return queryJob.result();
  }

  //endregion

}
