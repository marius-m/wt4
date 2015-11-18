package lt.markmerkk.utils;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusm on 10/30/14.
 * Represents the logic and core functionality of the clock
 */
public class HourGlass {
  public static final int TICK_DELAY = 1000;

  Timer timer = null;
  State state = State.STOPPED;
  long startMillis = 0;
  long endMillis = 0;
  long lastTick = 0;

  Listener listener;

  public HourGlass() { }

  //region Public

  /**
   * Starts timer
   *
   * @return reports if timer was started successfully
   */
  public boolean start() {
    if (state == State.STOPPED) {
      state = State.RUNNING;

      lastTick = current();
      endMillis = current();
      startMillis = current();
      TimerTask updateRunnable = new TimerTask() {
        @Override public void run() {
          Platform.runLater(() -> {
            update();
          });
        }
      };
      timer = new Timer();
      timer.scheduleAtFixedRate(updateRunnable, 1, TICK_DELAY);
      long delay = endMillis - startMillis;
      if (listener != null) listener.onStart(startMillis, endMillis, delay);
      return true;
    }
    return false;
  }

  /**
   * Stops timer
   *
   * @return reports if timer was stopped successfully
   */
  public boolean stop() {
    if (state == State.RUNNING) {
      state = State.STOPPED;
      timer.cancel();
      timer.purge();
      long delay = endMillis - startMillis;
      if (listener != null) listener.onStop(startMillis, endMillis, delay);
      startMillis = 0;
      endMillis = 0;
      lastTick = -1;
      return true;
    }
    return false;
  }

  /**
   * Restarts timer
   */
  public boolean restart() {
    stop();
    start();
    return true;
  }

  //endregion

  //region Core

  /**
   * A function to calculate duration and report a change
   */
  void update() {
    if (state == State.STOPPED) return;
    try {
      if (startMillis < 0)
        throw new TimeCalcError(Error.START);
      if (endMillis < 0)
        throw new TimeCalcError(Error.END);
      if (startMillis > endMillis)
        throw new TimeCalcError(Error.DURATION);
      endMillis += calcTimeIncrease();
      long delay = endMillis - startMillis;
      if (listener != null) listener.onTick(startMillis, endMillis, delay);
    } catch (TimeCalcError e) {
      lastTick = current();
      if (listener != null) listener.onError(e.getError());
    }
  }

  /**
   * Calculates time increase for the tick
   *
   * @return time increase
   */
  long calcTimeIncrease() {
    if (lastTick < 0) return 0;
    if (current() < 0) return 0;
    if (current() < lastTick) return 0;
    long increase = current() - lastTick;
    lastTick = current();
    return increase;
  }

  long current() {
    return DateTimeUtils.currentTimeMillis();
  }

  //endregion

  //region Getters / Setters

  private final DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");
  /**
   * Updates timers with input start, end times and todays date.
   * @param today provided todays date
   * @param startTime provided start time in string
   * @param endTime provided end time in string
   */
  public void updateTimers(DateTime today, String startTime, String endTime) {
    if (today == null)
      throw new IllegalArgumentException("Incorrect updateTimers use!");
    if (startTime == null)
      throw new IllegalArgumentException("Incorrect updateTimers use!");
    if (endTime == null)
      throw new IllegalArgumentException("Incorrect updateTimers use!");
    // Parsing start time
    try {
      DateTime start = shortFormat.parseDateTime(startTime).withDate(
          today.year().get(),
          today.monthOfYear().get(),
          today.dayOfMonth().get()
      );
      startMillis = start.getMillis();
    } catch (IllegalArgumentException e) {
      startMillis = -1;
    }

    // Parsing end time
    try {
      DateTime end = shortFormat.parseDateTime(endTime).withDate(
          today.year().get(),
          today.monthOfYear().get(),
          today.dayOfMonth().get()
      );
      endMillis = end.getMillis();
    } catch (IllegalArgumentException e) {
      endMillis = -1;
    }

    update();
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public State getState() {
    return state;
  }

  public long getStartMillis() {
    return startMillis;
  }

  //endregion

  //region Convenience

  /**
   * Appends a minute to a start time
   */
  public void appendMinute() {
    if ((startMillis + 1000 * 60) < current()) {
      startMillis += 1000 * 60; // Adding 60 seconds
    }
    update();
  }

  /**
   * Subtracts a minute from the time
   */
  public void subractMinute() {
    startMillis -= 1000 * 60; // Adding 60 seconds
    update();
  }

  //endregion

  //region Classes

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
    void onError(Error error);
  }

  /**
   * Represents the state that timer is in
   */
  public enum State {
    STOPPED(0),
    RUNNING(1);
    private int value;

    private State(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static State parseState(int status) {
      switch (status) {
        case 0:
          return STOPPED;
        case 1:
          return RUNNING;
        default:
          return STOPPED;
      }
    }
  }

  /**
   * Represents an error when calculating a time change.
   */
  public enum Error {
    START("Error in start time!"), END("Error in end time!"), DURATION(
        "Error calculating duration!");
    String message;

    Error(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }

  /**
   * Represents an error thrown when trying to calculate time.
   */
  private class TimeCalcError extends IllegalStateException {
    Error error;

    public TimeCalcError(Error error) {
      super(error.getMessage());
      this.error = error;
    }

    public Error getError() {
      return error;
    }
  }

  //endregion
}
