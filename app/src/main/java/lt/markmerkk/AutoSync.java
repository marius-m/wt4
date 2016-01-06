package lt.markmerkk;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.jira.WorkExecutor;
import lt.markmerkk.listeners.WorldEvents;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by mariusmerkevicius on 1/6/16.
 * Responsible for executing a scheduled {@link WorkExecutor}
 */
public class AutoSync implements WorldEvents {
  public static final String REFRESH_NEVER = "Never.";
  public static final String REFRESH_15 = "In 15 mins.";
  public static final String REFRESH_30 = "In 30 mins.";
  public static final String REFRESH_60 = "In 60 mins.";

  Log log = LogFactory.getLog(AutoSync.class);

  final ScheduledExecutorService scheduledExecutorService;
  ScheduledFuture<?> scheduledFuture;
  Listener listener;

  int interval;
  TimeUnit timeUnit;

  public AutoSync() {
    scheduledExecutorService = Executors.newScheduledThreadPool(1);
  }

  //region Public

  /**
   * Schedules new with predefined vars
   */
  public void schedule(String var) {
    if (var == null) {
      schedule(0, TimeUnit.MINUTES);
      return;
    }
    switch (var) {
      case REFRESH_15:
        schedule(15, TimeUnit.MINUTES);
        break;
      case REFRESH_30:
        schedule(30, TimeUnit.MINUTES);
        break;
      case REFRESH_60:
        schedule(60, TimeUnit.MINUTES);
        break;
      default:
        schedule(0, TimeUnit.MINUTES);
    }
  }

  /**
   * Schedules new trigger
   */
  void schedule(int interval, TimeUnit timeUnit) {
    if (scheduledFuture != null)
      scheduledFuture.cancel(true);
    scheduledFuture = null;
    log.info("New sync in "+interval+" "+timeUnit);
    if (interval == 0) return;
    this.interval = interval;
    this.timeUnit = timeUnit;
    scheduledFuture = scheduledExecutorService.schedule(trigger, interval, timeUnit);
  }

  /**
   * Stops scheduler
   */
  public void stop() {
    if (scheduledFuture != null)
      scheduledFuture.cancel(true);
    scheduledFuture = null;
    interval = 0;
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  //endregion

  @PostConstruct
  @Override
  public void onStart() {
  }

  @PreDestroy
  @Override
  public void onStop() {
    stop();
    scheduledExecutorService.shutdown();
  }

  //region Convenience

  Runnable trigger = new Runnable() {
    @Override
    public void run() {
      listener.onTrigger();
      scheduledFuture = null;
      schedule(interval, timeUnit);
    }
  };

  //endregion

  //region Classes

  public interface Listener {
    /**
     * Executed whenever scheduler hits.
     */
    void onTrigger();
  }

  //endregion

}
