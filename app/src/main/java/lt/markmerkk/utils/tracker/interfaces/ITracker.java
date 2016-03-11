package lt.markmerkk.utils.tracker.interfaces;

/**
 * Created by mariusmerkevicius on 3/11/16.
 * Abstract tracker interface
 */
public interface ITracker {

  /**
   * Sends a generic event
   * @param category
   * @param action
   * @param label
   * @param value
   */
  void sendEvent(String category, String action, String label, int value);

  /**
   * Sends a generic event
   * @param category
   * @param action
   */
  void sendEvent(String category, String action);

  /**
   * Stops tracker from working
   */
  void stop();
}
