package lt.markmerkk.utils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Simple controller for printing out last update.
 * Has a dependency for {@link UserSettingsImpl} as that is where
 * persistent data is stored.
 */
public class LastUpdateControllerImpl implements LastUpdateController {
  public static final String LAST_UPDATE = "LAST_UPDATE";

  @Inject
  UserSettings settings;

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
  @Override
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

  @Override
  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  @Override
  public boolean getLoading() {
    return loading;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Nullable
  @Override
  public String getError() {
    return error;
  }

  @NotNull
  @Override
  public String getOutput() {
    if (loading) return "Loading...";
    if (error != null) return "Error. "+error;
    if (lastUpdate == 0) return "Never";
    return Utils.formatShortDuration(durationTillLastUpdate());
  }

  @Override
  public long durationTillLastUpdate() {
    return now() - lastUpdate;
  }

  //endregion

}
