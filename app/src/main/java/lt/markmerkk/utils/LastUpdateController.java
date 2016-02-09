package lt.markmerkk.utils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Simple controller for printing out last update.
 * Has a dependency for {@link UserSettings} as that is where
 * persistent data is stored.
 */
public class LastUpdateController {
  public static final String LAST_UPDATE = "LAST_UPDATE";

  @Inject UserSettings settings;

  String error;
  boolean loading = false;
  //boolean error = false;
  long lastUpdate = 0;

  //region World event

  @PostConstruct
  void init() {
    try {
      lastUpdate = Long.parseLong(settings.getCustom(LAST_UPDATE));
    } catch (NumberFormatException e) {
      lastUpdate = 0;
    }
  }

  @PreDestroy
  void destroy() {
    settings.setCustom(LAST_UPDATE, String.format("%d", lastUpdate));
  }

  //endregion

  //region Public

  /**
   * Called whenever last update should be refreshed
   */
  public void refresh() {
    lastUpdate = now();
    settings.setCustom(LAST_UPDATE, String.format("%d", lastUpdate));
  }

  //endregion

  //region Convenience

  /**
   * Returns current time
   * @return
   */
  long now() {
    return DateTime.now().getMillis();
  }

  //endregion

  //region Getters / Setters

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getOutput() {
    if (loading) return "Loading...";
    if (error != null) return "Error. "+error;
    if (lastUpdate == 0) return "Never";
    return Utils.formatShortDuration(durationTillLastUpdate());
  }

  public long durationTillLastUpdate() {
    return now() - lastUpdate;
  }

  //endregion

}
