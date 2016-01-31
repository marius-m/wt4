package lt.markmerkk.storage2;

import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import net.rcarz.jiraclient.WorkLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/28/15. Responsible for merging / updating database {@link
 * SimpleLog} entities with the remote ones.
 *
 * This class should follow these rules for merging
 * 1. Create new local log if there is not current one
 * 2. Update local log with the data from sever
 * 3. All pulled data should contain dirty = 0.
 */
public class RemoteFetchMerger {
  Logger logger = LoggerFactory.getLogger(RemoteFetchMerger.class);

  IExecutor executor;

  public RemoteFetchMerger(IExecutor executor) {
    if (executor == null)
      throw new IllegalArgumentException("executor == null");
    this.executor = executor;
  }

  /**
   * Merges remote {@link WorkLog} and local {@link SimpleLog}
   */
  public void merge(String remoteIssue, WorkLog remoteLog) {
    SimpleLog localLog = getLocalEntity(getRemoteId(remoteLog));
    if (localLog == null) {
      SimpleLog newLog = newLog(remoteIssue, remoteLog);
      executor.execute(new InsertJob(SimpleLog.class, newLog));
      logger.info("New remote log: " + "(" + remoteIssue + ")" + newLog);
      return;
    }
    SimpleLog updateLog = updateLog(localLog, remoteIssue, remoteLog);
    executor.execute(new UpdateJob(SimpleLog.class, updateLog));
    logger.info("Updating old log: " + "(" + remoteIssue + ")" + updateLog);
  }

  //region Convenience wrappers

  SimpleLog updateLog(SimpleLog localLog, String remoteIssue, WorkLog worklog) {
    return new SimpleLogBuilder(localLog, remoteIssue, worklog).build();
  }

  SimpleLog newLog(String remoteIssue, WorkLog worklog) {
    return new SimpleLogBuilder(remoteIssue, worklog).build();
  }

  long getRemoteId(WorkLog worklog) {
    return SimpleLogBuilder.parseWorklogUri(worklog.getSelf().toString());
  }

  /**
   * Pulls local log entity from database with the id that matches the server one
   *
   * @param remoteId remote entity id
   */
  SimpleLog getLocalEntity(long remoteId) {
    if (remoteId <= 0) return null;
    QueryJob<SimpleLog> queryJob = new QueryJob<>(SimpleLog.class, () -> "id = " + remoteId);
    executor.execute(queryJob);
    return queryJob.result();
  }

  //endregion

}
