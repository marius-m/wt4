package lt.markmerkk.entities;

import lt.markmerkk.entities.database.interfaces.IExecutor;
import lt.markmerkk.entities.jobs.InsertJob;
import lt.markmerkk.entities.jobs.QueryJob;
import lt.markmerkk.entities.jobs.UpdateJob;
import net.rcarz.jiraclient.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 2/16/16.
 * Responsible for merging remote {@link Issue} with local {@link LocalIssue}
 */
@Deprecated
public class RemoteFetchIssue extends RemoteFetch<LocalIssue, Issue> {
  Logger logger = LoggerFactory.getLogger(RemoteFetchIssue.class);

  IExecutor executor;
  long downloadMillis;

  public RemoteFetchIssue(IExecutor executor, long downloadMillis) {
    if (executor == null)
      throw new IllegalArgumentException("executor == null");
    if (downloadMillis == 0)
      throw new IllegalArgumentException("downloadMillis == 0");
    this.executor = executor;
    this.downloadMillis = downloadMillis;
  }

  public long getDownloadMillis() {
    return downloadMillis;
  }

  @Override
  protected boolean entityNew(Issue remoteEntity) {
    logger.debug("Adding new issue: "+remoteEntity);
    LocalIssue newIssue = new LocalIssueBuilder(remoteEntity)
        .setDownloadMillis(downloadMillis)
        .build();
    InsertJob insertIssueJob = new InsertJob(LocalIssue.class, newIssue);
    executor.execute(insertIssueJob);
    return false; // value is not calculated
  }

  @Override
  protected boolean entityUpdate(LocalIssue localEntity, Issue remoteEntity) {
    logger.debug("Updating old issue: "+remoteEntity);
    LocalIssue updateIssue = new LocalIssueBuilder(localEntity, remoteEntity)
        .setDownloadMillis(downloadMillis)
        .build();
    UpdateJob updateIssueJob = new UpdateJob(LocalIssue.class, updateIssue);
    executor.execute(updateIssueJob);
    return false; // value is not calculated
  }

  @Override
  protected String localEntityId(Issue remoteEntity) {
    if (remoteEntity == null)
      return null;
    return remoteEntity.getId();
  }

  @Override
  protected LocalIssue localEntity(String remoteId) {
    if (remoteId == null)
      return null;
    QueryJob<LocalIssue> queryJob = new QueryJob<>(LocalIssue.class, () -> "id = " + remoteId);
    executor.execute(queryJob);
    return queryJob.result();
  }
}
