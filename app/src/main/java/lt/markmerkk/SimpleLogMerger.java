package lt.markmerkk;

import com.atlassian.jira.rest.client.api.domain.Worklog;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.database.DBBaseExecutor;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Responsible for merging / updating database {@link SimpleLog} entities
 * with the remote ones.
 *
 * This class should follow these rules for merging
 * 1. Create new local log if there is not current one
 * 2. Update local log with the data from sever
 * 3. All pulled data should contain dirty = 0.
 *
 */
public class SimpleLogMerger implements IMerger {
  IExecutor executor;
  String remoteIssue;
  Worklog worklog;

  public SimpleLogMerger(IExecutor executor, String remoteIssue, Worklog remoteWorklog) {
    if (executor == null)
      throw new IllegalArgumentException("Cannot function without database!");
    if (remoteIssue == null)
      throw new IllegalArgumentException("Cannot function without issue name!");
    if (remoteWorklog == null)
      throw new IllegalArgumentException("Cannot function without remote worklog!");
    this.executor = executor;
    this.remoteIssue = remoteIssue;
    this.worklog = remoteWorklog;
  }

  @Override public String merge() {
    SimpleLog localEntity = getLocalEntity(getRemoteId(worklog));
    if (localEntity == null) {
      SimpleLog newLog = newLog(remoteIssue, worklog);
      executor.execute(new InsertJob(SimpleLog.class, newLog));
      return "Creating new log: "+newLog;
    }
    //SimpleLog newLog = new SimpleLogBuilder(remoteIssue, worklog).build();
    //executor.execute(new InsertJob(SimpleLog.class, newLog));
    //return "Creating new log: "+newLog;
    return "Updating old worklog";
  }

  // We use wrappers as convenience and dedicated jobs for other classes
  //region Convenience wrappers

  SimpleLog newLog(String remoteIssue, Worklog worklog) {
    return new SimpleLogBuilder(remoteIssue, worklog).build();
  }

  long getRemoteId(Worklog worklog) {
    return SimpleLogBuilder.parseWorklogUri(worklog.getSelf().toString());
  }

  /**
   * Pulls local log entity from database with the id that matches the server one
   * @param remoteId remote entity id
   * @return
   */
  SimpleLog getLocalEntity(long remoteId) {
    if (remoteId <= 0) return null;
    QueryJob<SimpleLog> queryJob = new QueryJob<>(SimpleLog.class, () -> "id = " + remoteId);
    executor.execute(queryJob);
    return queryJob.result();
  }

  //endregion

}
