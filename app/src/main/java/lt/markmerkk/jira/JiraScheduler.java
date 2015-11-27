package lt.markmerkk.jira;

import java.util.LinkedList;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IScheduler;
import lt.markmerkk.jira.interfaces.IWorker;

/**
 * Created by mariusmerkevicius on 11/27/15.
 * Represents all the jobs in the list that must be done
 * to complete some long task.
 */
public class JiraScheduler implements IScheduler {
  private final ICredentials credentials;
  private final String name;
  LinkedList<IWorker> workers;

  public JiraScheduler(String name, ICredentials credentials, IWorker... inputWorkers) {
    if (name == null)
      throw new IllegalArgumentException("Cannot init scheduler without a name!");
    if (credentials == null)
      throw new IllegalArgumentException("Cannot init scheduler without credentials!");
    this.credentials = credentials;
    this.name = name;
    workers = new LinkedList<>();
    for (int i = inputWorkers.length-1; i >= 0; i--) {
      IWorker inputWorker = inputWorkers[i];
      if (inputWorker != null) workers.push(inputWorker);
    }
  }

  @Override public IWorker next() {
    IWorker worker = workers.peek();
    if (worker != null)
      worker.populateCredentials(credentials());
    return worker;
  }

  @Override public boolean hasMore() {
    return (workers.size() > 0);
  }

  @Override public ICredentials credentials() {
    return credentials;
  }

  @Override public String name() {
    return this.name;
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
