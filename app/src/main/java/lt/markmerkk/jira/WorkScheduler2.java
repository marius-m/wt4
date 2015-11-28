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
public class WorkScheduler2 implements IScheduler2 {

  ICredentials credentials;
  List<IWorker> workers;

  public WorkScheduler2(ICredentials credentials, IWorker... inputWorkers) {
    if (credentials == null)
      throw new IllegalArgumentException("Cannot init scheduler without credentials!");
    this.credentials = credentials;
    workers = new LinkedList<>();
    for (IWorker inputWorker : inputWorkers)
      if (inputWorker != null) workers.add(inputWorker);
  }

  @Override public boolean shouldExecute() {
    return workers.size() > 0;
  }

  @Override public IWorker nextWorker() {
    if (!shouldExecute()) return null;
    if (workers.size() == 0) return null;
    IWorker worker = workers.get(0);
    worker.populateCredentials(credentials);
    return worker;
  }

  @Override public void handleResult(IWorkerResult result) {
    IWorker executionWorker = nextWorker();
    try {
      if (executionWorker == null) throw new IllegalStateException("No execution workers!");
      if (result == null) throw new IllegalStateException("Result is null!");
      if (!result.isSuccess()) throw new IllegalStateException("Result response has failed!");
      if (!(result.tag().equals(executionWorker.tag()))) throw new IllegalStateException("Tags does not match!");
      workers.remove(executionWorker);
    } catch (IllegalStateException e) {
      workers.clear();
      System.out.println(e.getMessage());
    }
  }

  @Override public void reset() {
    workers.clear();
  }
}
