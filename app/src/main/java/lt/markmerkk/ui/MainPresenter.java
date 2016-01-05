package lt.markmerkk.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.clock.ClockPresenter;
import lt.markmerkk.ui.clock.ClockView;
import lt.markmerkk.ui.display.DisplayLogPresenter;
import lt.markmerkk.ui.display.DisplayLogView;
import lt.markmerkk.ui.settings.SettingsView;
import lt.markmerkk.ui.status.StatusView;
import lt.markmerkk.ui.update.UpdateLogPresenter;
import lt.markmerkk.ui.update.UpdateLogView;
import lt.markmerkk.utils.HiddenTabsController;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the main presenter of the app
 */
public class MainPresenter implements Initializable {

  @FXML TabPane tabPane;
  @FXML BorderPane northPane;
  @FXML BorderPane southPane;

  HiddenTabsController tabsController;

  public MainPresenter() {
    tabsController = new HiddenTabsController();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    ClockView clockView = new ClockView(clockListener);
    northPane.setCenter(clockView.getView());
    StatusView statusView = new StatusView();
    southPane.setBottom(statusView.getView());

    displayLogs();
    tabsController.prepare(tabPane);
  }

  //region Convenience

  /**
   * Displays all the logs
   */
  private void displayLogs() {
    DisplayLogView simpleLogView = new DisplayLogView(displayListener);
    southPane.setCenter(simpleLogView.getView());
  }

  /**
   * Displays update log window
   */
  private void updateLog(SimpleLog simpleLog) {
    UpdateLogView updateLogView = new UpdateLogView(updateListener, simpleLog);
    southPane.setCenter(updateLogView.getView());
  }

  //endregion

  //region Listeners

  ClockPresenter.Listener clockListener = new ClockPresenter.Listener() {
    @Override public void onNew() {
      tabsController.addCloseableTab(new SettingsView(), "Settings");
    }
  };

  UpdateLogPresenter.Listener updateListener = new UpdateLogPresenter.Listener() {
    @Override public void onFinish() {
      displayLogs();
    }
  };

  DisplayLogPresenter.Listener displayListener = new DisplayLogPresenter.Listener() {
    @Override public void onUpdate(SimpleLog object) {
      updateLog(object);
    }

  };

  //endregion


}
