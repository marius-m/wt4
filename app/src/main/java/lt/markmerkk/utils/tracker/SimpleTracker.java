package lt.markmerkk.utils.tracker;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import lt.markmerkk.Main;
import lt.markmerkk.utils.tracker.interfaces.ITracker;

/**
 * Created by mariusmerkevicius on 3/11/16.
 * Single instance of {@link GoogleAnalytics}
 */
public class SimpleTracker {
  public static final String CATEGORY_BUTTON = "BUTTON";
  public static final String CATEGORY_GENERIC = "GENERIC";
  //public static final String ACTION_ENTER = "ENTER_FROM_DAY";
  public static final String ACTION_ENTER_FROM_DAY = "ENTER_FROM_DAY";
  public static final String ACTION_ENTER_FROM_WEEK = "ENTER_FROM_WEEK";
  public static final String ACTION_SYNC_MAIN = "SYNC_MAIN";
  public static final String ACTION_SYNC_SETTINGS = "SYNC_SETTINGS";
  public static final String ACTION_SEARCH_REFRESH = "SEARCH_REFRESH";
  public static final String ACTION_START = "START";
//  public static final String LABEL_VIEW_DAY = "VIEW_DAY";
//  public static final String LABEL_VIEW_WEEK = "VIEW_WEEK";

  public static SimpleTracker instance;
  private ITracker tracker;

  private SimpleTracker() {
    tracker = (Main.DEBUG) ? new NullTracker() : new GATracker();
  }

  public static SimpleTracker getInstance() {
    if (instance == null)
      instance = new SimpleTracker();
    return instance;
  }

  public ITracker getTracker() {
    return tracker;
  }
}
