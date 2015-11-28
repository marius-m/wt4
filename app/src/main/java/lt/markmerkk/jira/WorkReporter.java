package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IWorkReporter;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Represents a default work reporter
 */
public class WorkReporter implements IWorkReporter {
  @Override public String reportMessage(String message) {
    if (message == null) return "";
    return message;
  }

  @Override public String reportWorkStart(IWorker worker) {
    if (worker == null) return "Error getting worker!";
    String message = null;
    try {
      message = worker.preExecuteMessage();
      if (message == null) return "";
      return message;
    } catch (Exception e) {
      return "Error: "+e.getMessage();
    }
  }

  @Override public String reportWorkEnd(IWorker worker, IWorkerResult result) {
    if (worker == null) return "Error getting worker!";
    if (result == null) return "Error getting result!";
    try {
      String message = worker.postExecuteMessage(result);
      if (message == null) return "";
      return message;
    } catch (Exception e) {
      return "Error: "+e.getMessage();
    }
  }
}
