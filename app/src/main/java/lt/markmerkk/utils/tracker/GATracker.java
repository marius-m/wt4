package lt.markmerkk.utils.tracker;

import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import lt.markmerkk.Main;
import lt.markmerkk.utils.tracker.interfaces.ITracker;

/**
 * Created by mariusmerkevicius on 3/11/16.
 * Represents tracker for tracking various events through {@link GoogleAnalytics}
 */
public class GATracker implements ITracker {
  final GoogleAnalytics analytics;

  public GATracker() {
    if (Main.GA_KEY == null)
      throw new IllegalArgumentException("ga_key == null");
    analytics = new GoogleAnalytics(Main.GA_KEY, Main.APP_NAME, Main.VERSION_NAME);
  }

  @Override
  public void sendEvent(String category, String action, String label, int value) {
    EventHit eventHit = new EventHit(category, action, label, value);
    analytics.postAsync(eventHit);
  }

  @Override
  public void sendEvent(String category, String action) {
    EventHit eventHit = new EventHit(category, action);
    analytics.postAsync(eventHit);
  }

  @Override
  public void stop() {
    analytics.close();
  }
}
