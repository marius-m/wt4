package lt.markmerkk.utils.hourglass;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javax.annotation.PreDestroy;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.exceptions.TimeCalcError;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusm on 10/30/14.
 * Represents the logic and core functionality of the clock
 */
public class HourGlass {
  public static final Logger logger = LoggerFactory.getLogger(HourGlass.class);
  public static final String DATE_SHORT_FORMAT = "HH:mm";
  public static final String DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm";

  public final static DateTimeFormatter shortFormat = DateTimeFormat.forPattern(DATE_SHORT_FORMAT);
  public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern(DATE_LONG_FORMAT);

  public static final int DEFAULT_TICK = 1000;

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
      timer.scheduleAtFixedRate(updateRunnable, 1, DEFAULT_TICK);
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
    long lastEnd = endMillis;
    stop();
    start();
    startMillis = lastEnd;
    endMillis = current();
    logger.debug("Restarting timer. Start: {} / End: {}", startMillis, endMillis);
    update();
    return true;
  }

  /**
   * Checks if all variables are valid
   * @return
   */
  public boolean isValid() {
    try {
      checkAndThrowForError();
      return true;
    } catch (TimeCalcError timeCalcError) { }
    return false;
  }

  @PreDestroy
  public void destroy() {
    if (state == State.RUNNING)
      stop();
  }

  //endregion

  //region Core

  /**
   * A function to calculate duration and report a change
   */
  void update() {
    if (state == State.STOPPED) return;
    try {
      checkAndThrowForError();
      endMillis += calcTimeIncrease();
      long delay = endMillis - startMillis;
      if (listener != null) listener.onTick(startMillis, endMillis, delay);
      logger.debug("Tick tock: start {} / end {}", startMillis, endMillis);
    } catch (TimeCalcError e) {
      logger.debug("Timer update error: start {} / end {}", startMillis, endMillis);
      lastTick = current();
      if (listener != null) listener.onError(e.getError());
    }
  }

  /**
   * Checks if all variables are correct for counting
   * @throws TimeCalcError
   */
  void checkAndThrowForError() throws TimeCalcError {
    if (startMillis < 0)
      throw new TimeCalcError(Error.START);
    if (endMillis < 0)
      throw new TimeCalcError(Error.END);
    if (startMillis > endMillis)
      throw new TimeCalcError(Error.DURATION);
  }

  /**
   * Calculates time increase for the tick
   *
   * @return time increase
   */
  long calcTimeIncrease() {
    if (lastTick < 0) return DEFAULT_TICK;
    if (current() < 0) return DEFAULT_TICK;
    if (current() < lastTick) return DEFAULT_TICK;
    long increase = current() - lastTick;
    lastTick = current();
    return increase;
  }

  long current() {
    return DateTimeUtils.currentTimeMillis();
  }

  //endregion

  //region Getters / Setters

  /**
   * Sets current day for the hourglass
   * @param currentDay provided current day
   */
  public void setCurrentDay(DateTime currentDay) {
    if (currentDay == null)
      throw new IllegalArgumentException("Cannot set current day without provided date!");
    try {
      startMillis = new DateTime(startMillis).withDate(
          currentDay.year().get(),
          currentDay.monthOfYear().get(),
          currentDay.dayOfMonth().get()
      ).getMillis();
      endMillis = new DateTime(endMillis).withDate(
          currentDay.year().get(),
          currentDay.monthOfYear().get(),
          currentDay.dayOfMonth().get()
      ).getMillis();
      update();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reports start time in {@link DateTime}
   * @return start time
   */
  public DateTime reportStart() {
    if (isValid())
      return new DateTime(startMillis);
    return new DateTime(current());
  }

  /**
   * Reports end time in {@link DateTime}
   * @return end time
   */
  public DateTime reportEnd() {
    if (isValid())
      return new DateTime(endMillis);
    return new DateTime(current());
  }

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

      // Correct time with current millis
      long currentSeconds = (current() - DateTime.now().withSecondOfMinute(0).getMillis());
      endMillis += currentSeconds;
    } catch (IllegalArgumentException e) {
      endMillis = -1;
    }

    update();
  }

  /**
   * Updates timers with input start, end times.
   * @param startTime provided start time in string
   * @param endTime provided end time in string
   */
  public void updateTimers(String startTime, String endTime) {
    if (startTime == null)
      throw new IllegalArgumentException("Incorrect updateTimers use!");
    if (endTime == null)
      throw new IllegalArgumentException("Incorrect updateTimers use!");
    // Parsing start time
    try {
      DateTime start = longFormat.parseDateTime(startTime);
      startMillis = start.getMillis();
    } catch (IllegalArgumentException e) {
      startMillis = -1;
    }

    // Parsing end time
    try {
      DateTime end = longFormat.parseDateTime(endTime);
      endMillis = end.getMillis();

      // Correct time with current millis
      long currentSeconds = (current() - DateTime.now().withSecondOfMinute(0).getMillis());
      endMillis += currentSeconds;
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

  public long getEndMillis() {
    return endMillis;
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

  /**
   * Parses time output and converts it into millis
   * @param time input formatted time
   * @return
   */
  public static long parseMillisFromText(String time) {
    if (Utils.isEmpty(time)) return -1;
    try {
      return longFormat.parseDateTime(time).getMillis();
    } catch (IllegalArgumentException e) {
      return -1;
    }
  }

  //endregion

  //region Classes

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

  //endregion

  //region Classes

  /**
   * Public listener that reports the changes
   */
  public static interface Listener {
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

  //endregion
}
