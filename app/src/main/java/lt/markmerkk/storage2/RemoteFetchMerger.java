package lt.markmerkk.storage2;

import lt.markmerkk.listeners.IMerger;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import net.rcarz.jiraclient.WorkLog;

/**
 * Created by mariusmerkevicius on 11/28/15. Responsible for merging / updating database {@link
 * SimpleLog} entities with the remote ones.
 *
 * This class should follow these rules for merging
 * 1. Create new local log if there is not current one
 * 2. Update local log with the data from sever
 * 3. All pulled data should contain dirty = 0.
 */
public class RemoteFetchMerger implements IMerger {
  IExecutor executor;
  String remoteIssue;
  WorkLog worklog;

  public RemoteFetchMerger(IExecutor executor, String remoteIssue, WorkLog remoteWorklog) {
    if (executor == null)
      throw new IllegalArgumentException("executor == null");
    if (remoteIssue == null)
      throw new IllegalArgumentException("remoteIssue == null");
    if (remoteWorklog == null)
      throw new IllegalArgumentException("remoteWorkLog == null");
    this.executor = executor;
    this.remoteIssue = remoteIssue;
    this.worklog = remoteWorklog;
  }

  @Override
  public String merge() {
    SimpleLog localLog = getLocalEntity(getRemoteId(worklog));
    if (localLog == null) {
      SimpleLog newLog = newLog(remoteIssue, worklog);
      executor.execute(new InsertJob(SimpleLog.class, newLog));
      return "Creating new log: " + newLog;
    }
    SimpleLog updateLog = updateLog(localLog, remoteIssue, worklog);
    executor.execute(new UpdateJob(SimpleLog.class, updateLog));
    return "Updating old worklog: " + updateLog;
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
