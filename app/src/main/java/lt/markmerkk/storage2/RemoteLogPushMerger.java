package lt.markmerkk.storage2;

import com.google.common.base.Strings;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/28/15. Responsible for merging / updating database {@link
 * SimpleLog} entities with the remote ones.
 * @deprecated use {@link lt.markmerkk.merger.RemoteLogPushImpl} instead
 */
@Deprecated
public class RemoteLogPushMerger {
  Logger logger = LoggerFactory.getLogger(RemoteLogPushMerger.class);
  IExecutor executor;
  JiraClient client;

  public RemoteLogPushMerger(IExecutor executor, JiraClient client) {
    if (executor == null)
      throw new IllegalArgumentException("Cannot function without database!");
    if (client == null)
      throw new IllegalArgumentException("Cannot function without a Jira client!");
    this.client = client;
    this.executor = executor;
  }

  /**
   * Merges {@link SimpleLog} with the remote
   */
  public void merge(SimpleLog localLog) {
    if (!isValid(localLog)) return;
    logger.info("Uploading " + localLog);
    upload(localLog);
  }

  /**
   * Validates local log before upload
   *
   * @param localLog provided local log
   */
  private boolean isValid(SimpleLog localLog) {
    if (Strings.isNullOrEmpty(localLog.getComment())) {
      logger.debug("Skipping " + localLog + "as it doesn't have a comment!");
      executor.execute(new UpdateJob(SimpleLog.class,
          new SimpleLogBuilder(localLog).buildWithError("Does not have a comment!")));
      return false;
    }
    if (Strings.isNullOrEmpty(localLog.getTask())) {
      logger.debug("Skipping " + localLog + "as it doesn't have an issue!");
      executor.execute(new UpdateJob(SimpleLog.class,
          new SimpleLogBuilder(localLog).buildWithError("Does not have a linked issue!")));
      return false;
    }
    if (!localLog.isDirty()) {
      logger.debug("Skipping " + localLog + "as it is already in sync!");
      return false;
    }
    if (localLog.isError()) {
      logger.debug("Skipping " + localLog + "as it already has an error!");
      return false;
    }
    return true;
  }

  /**
   * Uploads a log to the remote
   *
   * @param localLog provided local log
   */
  void upload(SimpleLog localLog) {
    Issue issue = null;
    try {
      issue = client.getIssue(localLog.getTask());
      String localComment = TimeSplit.addStamp(localLog.getStart(), localLog.getEnd(), localLog.getComment());
      WorkLog out = issue.addWorkLog(localComment,
          new DateTime(localLog.getStart()),
          localLog.getDuration() / 1000);
      executor.execute(new DeleteJob(SimpleLog.class, localLog));
      executor.execute(new InsertJob(SimpleLog.class,
          new SimpleLogBuilder(issue.getKey(), out).build()));
    } catch (JiraException e) {
      logger.info("Can't push " + localLog + ". " + e.getMessage());
      executor.execute(new UpdateJob(SimpleLog.class,
          new SimpleLogBuilder(localLog).buildWithError(e.getMessage())));
    }
  }

}