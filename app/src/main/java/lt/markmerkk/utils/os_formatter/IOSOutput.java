package lt.markmerkk.utils.os_formatter;

/**
 * Created by mariusmerkevicius on 11/24/15.
 * An interface that outputs time log to OS specific implementation
 */
public interface IOSOutput {
  /**
   * Called when duration message changes. (for ex.: show on badge like in osx).
   * @param message provided message to display
   */
  void onDurationMessage(String message);
}
