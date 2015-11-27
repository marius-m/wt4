package lt.markmerkk.jira;

import java.util.LinkedList;
import java.util.List;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * A scheduler that schedules {@link IWorker} for execution
 */
public class JiraScheduler2 implements IScheduler2 {

  ICredentials credentials;
  List<IWorker> workers;

  public JiraScheduler2(ICredentials credentials, IWorker... inputWorkers) {
    if (credentials == null)
      throw new IllegalArgumentException("Cannot init scheduler without credentials!");
    this.credentials = credentials;

    workers = new LinkedList<>();
    for (IWorker inputWorker : inputWorkers)
      if (inputWorker != null) workers.add(inputWorker);
  }

  @Override public boolean shouldContinueExecution() {
    return false;
  }

  @Override public boolean shouldStartExecution() {
    return workers.size() > 0;
  }

  @Override public IWorker startExecution() {
    if (!shouldStartExecution()) return null;
    if (workers.size() == 0) return null;
    return workers.get(0);
  }

  @Override public void handleResult(IWorkerResult result) {

  }
}
