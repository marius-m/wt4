package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * A mock currentSchedulerOrEmptyOne to avoid null checks
 */
public class NullWorkScheduler2 implements IScheduler2 {

  public NullWorkScheduler2() { }

  @Override public boolean shouldExecute() {
    return false;
  }

  @Override public IWorker nextWorker() {
    return null;
  }

  @Override public void handleResult(IWorkerResult result) throws IllegalStateException { }

  @Override public void reset() { }
}
