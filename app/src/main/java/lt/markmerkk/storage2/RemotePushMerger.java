package lt.markmerkk.storage2;

import com.google.common.base.Strings;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import lt.markmerkk.utils.Utils;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Responsible for merging / updating database {@link SimpleLog} entities
 * with the remote ones.
 */
public class RemotePushMerger {
  Logger logger = LoggerFactory.getLogger(RemotePushMerger.class);
  IExecutor executor;
  JiraClient client;

  public RemotePushMerger(IExecutor executor, JiraClient client) {
    if (executor == null)
      throw new IllegalArgumentException("Cannot function without database!");
    if (client == null)
      throw new IllegalArgumentException("Cannot function without a Jira client!");
    this.client = client;
    this.executor = executor;
  }

  /**
   * Merges {@link SimpleLog} with the remote
   * @param localLog
   */
  public void merge(SimpleLog localLog) {
    if (!isValid(localLog)) return;
    logger.info("Uploading "+localLog);
    upload(localLog);
  }

  /**
   * Validates local log before upload
   * @param localLog provided local log
   * @return
   */
  private boolean isValid(SimpleLog localLog) {
    if (Strings.isNullOrEmpty(localLog.getComment())) {
      logger.debug("Skipping "+localLog+"as it doesn't have a comment!");
      executor.execute(new UpdateJob(SimpleLog.class,
          new SimpleLogBuilder(localLog).buildWithError("Does not have a comment!")));
      return false;
    }
    if (Strings.isNullOrEmpty(localLog.getTask())) {
      logger.debug("Skipping "+localLog+"as it doesn't have an issue!");
      executor.execute(new UpdateJob(SimpleLog.class,
          new SimpleLogBuilder(localLog).buildWithError("Does not have a linked issue!")));
      return false;
    }
    if (!localLog.isDirty()) {
      logger.debug("Skipping "+localLog+"as it is already in sync!");
      return false;
    }
    if (localLog.isError()) {
      logger.debug("Skipping "+localLog+"as it already has an error!");
      return false;
    }
    return true;
  }

  /**
   * Uploads a log to the remote
   * @param localLog provided local log
   */
  void upload(SimpleLog localLog) {
    Issue issue = null;
    try {
      issue = client.getIssue(localLog.getTask());
      WorkLog out = issue.addWorkLog(localLog.getComment(),
          new DateTime(localLog.getStart()),
          localLog.getDuration() / 1000);
      executor.execute(new DeleteJob(SimpleLog.class, localLog));
      executor.execute(new InsertJob(SimpleLog.class,
          new SimpleLogBuilder(issue.getKey(), out).build()));
    } catch (JiraException e) {
      logger.info("Can't push "+localLog+". "+e.getMessage());
      executor.execute(new UpdateJob(SimpleLog.class,
          new SimpleLogBuilder(localLog).buildWithError(e.getMessage())));
    }
  }

  //region Convenience wrappers

//  /**
//   * Convenience method to pull the log that needs to be uploaded for the remote server
//   * @param executor database executor
//   * @return
//   */
//  SimpleLog newRemoteLog(IExecutor executor) {
//    // Returning all the jobs that are dirty and does not have a server
//    QueryJob<SimpleLog> queryJob = new QueryJob<>(SimpleLog.class,
//        () -> "(start > " + targetDate.getMillis()
//            + " AND "
//            + "end < " + targetDate.plusDays(1).getMillis()
//            + " AND "
//            + " dirty = 1"
//            + " AND "
//            + " id = 0"
//            + " AND "
//            + " error = 0"
//            + ")");
//    executor.execute(queryJob);
//    return queryJob.result();
//  }

  //endregion

}