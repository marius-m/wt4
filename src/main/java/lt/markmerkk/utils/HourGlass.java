package lt.markmerkk.utils;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import org.joda.time.DateTimeUtils;

/**
 * Created by mariusm on 10/30/14.
 * Represents the logic and core functionality of the clock
 */
public class HourGlass {
  public static final int TICK_DELAY = 1000;
  private Timer timer = null;
  protected State state = State.STOPPED;
  private long startMillis = 0;
  private Listener listener;

  public HourGlass() {
  }

  //region Public

  /**
   * Starts timer
   *
   * @return reports if timer was started successfully
   */
  public boolean start() {
    if (state == State.STOPPED) {
      state = State.RUNNING;
      startMillis = DateTimeUtils.currentTimeMillis();
      TimerTask updateRunnable = new TimerTask() {
        @Override public void run() {
          update();
        }
      };
      timer = new Timer();
      timer.scheduleAtFixedRate(updateRunnable, 1, TICK_DELAY);
      long delay = DateTimeUtils.currentTimeMillis() - startMillis;
      if (listener != null) listener.onStart(startMillis, DateTimeUtils.currentTimeMillis(), delay);
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
      update();
      long delay = DateTimeUtils.currentTimeMillis() - startMillis;
      if (listener != null) listener.onStop(startMillis, DateTimeUtils.currentTimeMillis(), delay);
      startMillis = 0;
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

  // Core update methods

  /**
   * A function to calculate duration and report a change
   */
  private void update() {
    Platform.runLater(() -> {
      if (state == State.STOPPED) return;
      long delay = DateTimeUtils.currentTimeMillis() - startMillis;
      if (listener != null)
        listener.onTick(startMillis, DateTimeUtils.currentTimeMillis(), delay);
    });
  }

  //region Getters / Setters

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

  /**
   * Appends a minute to a start time
   */
  public void appendMinute() {
    if ((startMillis + 1000 * 60) < DateTimeUtils.currentTimeMillis()) {
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

  //endregion
}
