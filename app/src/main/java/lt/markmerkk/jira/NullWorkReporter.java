package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IWorkReporter;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Represents a null implementation of the work reporter
 */
public class NullWorkReporter implements IWorkReporter {
  @Override public String reportMessage(String message) {
    return "";
  }

  @Override public String reportWorkStart(IWorker worker) {
    return "";
  }

  @Override public String reportWorkEnd(IWorker worker, IWorkerResult result) {
    return "";
  }
}
