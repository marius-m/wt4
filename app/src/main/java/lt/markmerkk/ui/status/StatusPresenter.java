package lt.markmerkk.ui.status;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lt.markmerkk.*;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.interactors.KeepAliveInteractor;
import lt.markmerkk.interactors.SyncInteractor;
import lt.markmerkk.interactors.VersioningInteractor;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.ui.clock.utils.SimpleDatePickerConverter;
import lt.markmerkk.utils.LogFormatters;
import lt.markmerkk.utils.LogUtils;
import lt.markmerkk.utils.tracker.ITracker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, IRemoteLoadListener,
    KeepAliveInteractor.Listener, VersioningInteractor.LoadingListener {
  public static final Logger logger = LoggerFactory.getLogger(StatusPresenter.class);

  @Inject
  LogStorage storage;
  @Inject
  ITracker tracker;
  @Inject
  SyncInteractor syncInteractor;
  @Inject
  KeepAliveInteractor keepAliveInteractor;
  @Inject
  Config config;

  @FXML ProgressIndicator outputProgress;
  @FXML ProgressIndicator versionLoadIndicator;
  @FXML Button buttonRefresh;
  @FXML ToggleButton buttonViewToggle;
  @FXML Button buttonToday;
  @FXML Button buttonAbout;
  @FXML DatePicker targetDatePicker;

  String total;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    Main.component().presenterComponent().inject(this);
    buttonRefresh.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_button_status")));
    buttonViewToggle.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_toggle_view")));
    buttonViewToggle.setOnMouseClicked(buttonViewToggleListener);
    buttonToday.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_button_total")));
    buttonAbout.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_button_about")));
    buttonAbout.setOnMouseClicked(aboutClickListener);
    versionLoadIndicator.setOnMouseClicked(aboutClickListener);
    syncInteractor.addLoadingListener(this);
    total = LogUtils.INSTANCE.formatShortDuration(storage.total());
    targetDatePicker.getEditor().setText(
            LogFormatters.INSTANCE.getShortFormatDate().print(storage.getTargetDate())
    );
    targetDatePicker.setConverter(new SimpleDatePickerConverter());
    targetDatePicker.getEditor().textProperty().addListener(
            (observable, oldValue, newValue) -> {
      storage.suggestTargetDate(targetDatePicker.getEditor().getText());
    });

    updateStatus();
    onVersionLoadChange(syncInteractor.isLoading());
    storage.register(loggerListener);
    keepAliveInteractor.register(this);
  }

  @PreDestroy
  public void destroy() {
    keepAliveInteractor.unregister(this);
    storage.unregister(loggerListener);
    syncInteractor.removeLoadingListener(this);
  }

  //endregion

  //region Keyboard input

  /**
   * A button event when user clicks on refresh
   */
  public void onClickRefresh() {
    syncInteractor.syncLogs();
    tracker.sendEvent(
            GAStatics.INSTANCE.getCATEGORY_BUTTON(),
            GAStatics.INSTANCE.getACTION_SYNC_MAIN()
    );
  }

  public void onClickGraph() {
    tracker.sendView(
            GAStatics.INSTANCE.getVIEW_GRAPH()
    );
    listener.onGraphs();
  }

  //endregion

  //region Convenience

  /**
   * Convenience method to update current status
   */
  void updateStatus() {
    buttonToday.setText(LogUtils.INSTANCE.formatShortDuration(storage.total()));
    buttonViewToggle.setSelected(storage.getDisplayType() == DisplayTypeLength.WEEK);
  }

  //endregion

  //region Listeners

  EventHandler<MouseEvent> buttonViewToggleListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      if (listener == null) return;
      DisplayTypeLength displayTypeLength = (storage.getDisplayType() == DisplayTypeLength.DAY) ? DisplayTypeLength.WEEK : DisplayTypeLength.DAY;
      storage.setDisplayType(displayTypeLength);
      listener.onDisplayType(displayTypeLength);
      updateStatus();
    }
  };

  IDataListener<SimpleLog> loggerListener = new IDataListener<SimpleLog>() {
    @Override
    public void onDataChange(@NotNull List<? extends SimpleLog> data) {
      total = LogUtils.INSTANCE.formatShortDuration(storage.total());
      updateStatus();
    }
  };

  EventHandler<MouseEvent> aboutClickListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      if (listener == null) return;
      listener.onAbout();
    }
  };

  //endregion

  //region Getters / Setters

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  @Override
  public void onVersionLoadChange(boolean loading) {
    Platform.runLater(() -> {
      versionLoadIndicator.setManaged(loading);
      versionLoadIndicator.setVisible(loading);
      updateStatus();
    });
  }

  @Override
  public void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      outputProgress.setManaged(loading);
      outputProgress.setVisible(loading);
      updateStatus();
    });
  }

  @Override
  public void onError(String error) {
    updateStatus();
  }

  @Override
  public void update() {
    updateStatus();
  }

  //endregion

  //region Classes

  /**
   * Helper listener for interacting with status
   * listener
   */
  public interface Listener {
    /**
     * Callback whenever display type is selected
     * @param type
     */
    void onDisplayType(DisplayTypeLength type);

    /**
     * Called when about is pressed
     */
    void onAbout();

    /**
     * Called when graphs is pressed
     */
    void onGraphs();

  }

  //endregion

}
