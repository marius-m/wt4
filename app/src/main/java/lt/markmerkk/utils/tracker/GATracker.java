package lt.markmerkk.utils.tracker;

import com.brsanthu.googleanalytics.AppViewHit;
import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.google.common.base.Strings;
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
    if (Strings.isNullOrEmpty(category)) return;
    if (Strings.isNullOrEmpty(action)) return;
    if (Strings.isNullOrEmpty(label)) return;
    analytics.postAsync(new EventHit(category, action, label, value));
  }

  @Override
  public void sendEvent(String category, String action) {
    if (Strings.isNullOrEmpty(category)) return;
    if (Strings.isNullOrEmpty(action)) return;
    analytics.postAsync(new EventHit(category, action));
  }

  public void sendView(String contentDescription) {
    if (Strings.isNullOrEmpty(contentDescription)) return;
    analytics.postAsync(new AppViewHit(Main.APP_NAME, Main.VERSION_NAME, contentDescription));
  }

  @Override
  public void stop() {
    analytics.close();
  }
}
