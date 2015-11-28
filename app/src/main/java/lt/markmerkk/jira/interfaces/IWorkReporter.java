package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Represents a reporter that reports various events
 * and outputs them as simple string.
 */
public interface IWorkReporter {
  /**
   * Responsible for outputting a simple string report
   * @param message provided message
   */
  String reportMessage(String message);

  /**
   * Responsible for outputting a pre execution message {@link IWorker#preExecuteMessage()}
   * @param worker
   */
  String reportWorkStart(IWorker worker);

  /**
   * Responsible for outputting a post execution message for {@link IWorker#postExecuteMessage(Object)}
   * with provided {@link IWorkerResult}
   * @param worker
   */
  String reportWorkEnd(IWorker worker, IWorkerResult result);

}
