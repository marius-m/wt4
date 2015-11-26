package lt.markmerkk.jira;

import java.util.LinkedList;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IScheduler;
import lt.markmerkk.jira.interfaces.IWorker;

/**
 * Created by mariusmerkevicius on 11/27/15.
 * Represents all the jobs in the list that must be done
 * to complete some long task.
 */
public class JiraScheduler implements IScheduler {

  LinkedList<IWorker> workers;

  public JiraScheduler(IWorker... inputWorkers) {
    workers = new LinkedList<>();
    for (int i = inputWorkers.length-1; i >= 0; i--) {
      IWorker inputWorker = inputWorkers[i];
      if (inputWorker != null) workers.push(inputWorker);
    }
  }

  @Override public boolean isComplete() {
    if (workers == null) return true;
    if (workers.size() == 0) return true;
    return false;
  }

  @Override public IWorker next() {
    return workers.peek();
  }

  @Override public LinkedList<IWorker> workers() {
    return workers;
  }

  @Override public IWorker complete(IResponse response) throws IllegalStateException {
    if (response == null) return null;
    IWorker worker = workers.peek();
    if (worker == null) return null;
    if (!(worker.tag().equals(response.tag()))) {
      workers.clear();
      throw new IllegalStateException("Error comparing IResponse and IWorker!");
    }
    workers.pop();
    return workers.peek();
  }
}
