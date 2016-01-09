package lt.markmerkk;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.jira.WorkExecutor;
import lt.markmerkk.utils.LastUpdateController;
import lt.markmerkk.utils.UserSettings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by mariusmerkevicius on 1/8/16.
 * Responsible for executing a scheduled {@link WorkExecutor}
 * This class does not have its own executor, and will use only calculation to
 * determine when to trigger {@link WorkExecutor}
 */
public class AutoSync2 {
  public static final String SETTINGS_UPDATE = "SETTINGS_UPDATE";

  public static final String REFRESH_NEVER = "Auto update: Disabled";
  public static final String REFRESH_15 = "Auto update: 15 mins.";
  public static final String REFRESH_30 = "Auto update: 30 mins.";
  public static final String REFRESH_60 = "Auto update: 60 mins.";

  public static final long TERM_MINUTE = 1000 * 60;
  public static final long TERM_NEVER = -1;
  public static final long TERM_15 = TERM_MINUTE * 15;
  public static final long TERM_30 = TERM_MINUTE * 30;
  public static final long TERM_60 = TERM_MINUTE * 60;

  @Inject LastUpdateController lastUpdateController;
  @Inject UserSettings settings;

  Map<String, Long> selections;
  String currentSelection = null;

  Log log = LogFactory.getLog(AutoSync2.class);

  public AutoSync2() {
    this.selections = new HashMap<>();
    selections.put(REFRESH_NEVER, TERM_NEVER);
    selections.put(REFRESH_15, TERM_15);
    selections.put(REFRESH_30, TERM_30);
    selections.put(REFRESH_60, TERM_60);
  }

  @PostConstruct
  void init() {
    currentSelection = settings.getCustom(SETTINGS_UPDATE);
  }

  @PreDestroy
  void destroy() {
    settings.setCustom(SETTINGS_UPDATE, currentSelection);
  }

  /**
   * Outputs if sync is needed
   */
  public boolean isSyncNeeded() {
    if (currentSelectionValue() == TERM_NEVER) return false;
    return (lastUpdateController.durationTillLastUpdate() >= currentSelectionValue());
  }

  //region Getters / Setters

  public ObservableList<String> getSelectionKeys() {
    return FXCollections.observableArrayList(selections.keySet());
  }

  public void setCurrentSelection(String currentSelection) {
    this.currentSelection = currentSelection;
  }

  public String currentSelection() {
    return (!selections.containsKey(currentSelection)) ? REFRESH_NEVER : currentSelection;
  }

  //endregion

  //region Convenience

  long currentSelectionValue() {
    return (!selections.containsKey(currentSelection)) ? TERM_NEVER : selections.get(currentSelection);
  }

  //endregion

}
