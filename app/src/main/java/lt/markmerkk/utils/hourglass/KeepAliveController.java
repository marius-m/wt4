package lt.markmerkk.utils.hourglass;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.utils.hourglass.exceptions.TimeCalcError;

/**
 * Created by mariusmerkevicius on 1/9/16.
 * A controller that ticks once per minute to update
 * some settings.
 */
public class KeepAliveController {
  public static final int DEFAULT_TICK = 1000;
  public static final int MINUTE = DEFAULT_TICK * 60;

  Timer timer = null;
  Listener listener;
  long delay = MINUTE;

  @PostConstruct
  public void start() {
    TimerTask updateRunnable = new TimerTask() {
      @Override public void run() {
        if (listener == null) return;
        Platform.runLater(() -> listener.onUpdate());
      }
    };
    timer = new Timer();
    timer.scheduleAtFixedRate(updateRunnable, 1, delay);
  }

  @PreDestroy
  public void stop() {
    if (timer == null) return;
    timer.cancel();
    timer = null;
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Callback listener
   */
  public interface Listener {
    /**
     * Event called whenever something must be updated regulary
     */
    void onUpdate();
  }

}
