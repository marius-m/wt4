package lt.markmerkk.utils.hourglass.interfaces;

import lt.markmerkk.utils.hourglass.HourGlass;

/**
 * Public listener that reports the changes
 */
public interface Listener {
  /**
   * Called when timer has been started
   *
   * @param start provided start time
   * @param end provided end time
   * @param duration provided duration
   */
  void onStart(long start, long end, long duration);

  /**
   * Called when timer has been stopped
   *
   * @param start provided start time
   * @param end provided end time
   * @param duration provided duration
   */
  void onStop(long start, long end, long duration);

  /**
   * Called on every second tick when timer is running
   *
   * @param start provided start time
   * @param end provided end time
   * @param duration provided duration
   */
  void onTick(long start, long end, long duration);

  /**
   * Reports an error when there is something wrong with calculation.
   */
  void onError(HourGlass.Error error);
}
