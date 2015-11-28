package lt.markmerkk.jira;

import java.util.LinkedList;
import java.util.List;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * A mock scheduler to avoid null checks
 */
public class NullWorkScheduler2 implements IScheduler2 {

  public NullWorkScheduler2() { }

  @Override public boolean shouldExecute() {
    return false;
  }

  @Override public IWorker nextWorker() {
    return null;
  }

  @Override public void handleResult(IWorkerResult result) { }

  @Override public void reset() { }
}
