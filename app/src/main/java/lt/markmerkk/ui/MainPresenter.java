package lt.markmerkk.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.clock.ClockPresenter;
import lt.markmerkk.ui.clock.ClockView;
import lt.markmerkk.ui.display.DisplayLogView;
import lt.markmerkk.ui.interfaces.UpdateListener;
import lt.markmerkk.ui.settings.SettingsView;
import lt.markmerkk.ui.status.StatusPresenter;
import lt.markmerkk.ui.status.StatusView;
import lt.markmerkk.ui.taskweb.TaskWebView;
import lt.markmerkk.ui.update.UpdateLogPresenter;
import lt.markmerkk.ui.update.UpdateLogView;
import lt.markmerkk.ui.utils.DisplayType;
import lt.markmerkk.ui.week.WeekView;
import lt.markmerkk.utils.HiddenTabsController;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the main presenter of the app
 */
public class MainPresenter implements Initializable {

  @FXML TabPane tabPane;
  @FXML BorderPane northPane;
  @FXML BorderPane southPane;

  Stage stage;
  Popup popup;
  HiddenTabsController tabsController;
  DisplayType displayType = DisplayType.DAY;

  public MainPresenter() {
    tabsController = new HiddenTabsController();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    popup = new Popup();
    ClockView clockView = new ClockView(clockListener);
    northPane.setCenter(clockView.getView());
    southPane.setBottom(statusView.getView());

    displayLogs();
    tabsController.prepare(tabPane);
  }

  //region Convenience

  /**
   * Displays all the logs
   */
  private void displayLogs() {
    switch (displayType) {
      case DAY:
        DisplayLogView simpleLogView = new DisplayLogView(updateListener);
        southPane.setCenter(simpleLogView.getView());
        break;
      case WEEK:
        WeekView weekView = new WeekView(updateListener);
        southPane.setCenter(weekView.getView());
        break;
    }
  }

  /**
   * Displays update log window
   */
  private void updateLog(SimpleLog simpleLog) {
    UpdateLogView updateLogView = new UpdateLogView(updateWindowListener, simpleLog);
    popup.getContent().addAll(updateLogView.getView());
    popup.show(stage);
  }

  //endregion

  //region Getters / Setters

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  //endregion

  //region Listeners

  ClockPresenter.Listener clockListener = new ClockPresenter.Listener() {
    @Override
    public void onSettings() {
      tabsController.addCloseableTab(new SettingsView(), "Settings");
    }

    @Override
    public void onOpen(String url, String title) {
      tabsController.addCloseableTab(new TaskWebView(url), title);
    }

  };

  UpdateLogPresenter.Listener updateWindowListener = new UpdateLogPresenter.Listener() {
    @Override public void onFinish() {
      if (popup.isShowing())
        popup.hide();
      displayLogs();
    }
  };

  UpdateListener updateListener = new UpdateListener() {
    @Override public void onUpdate(SimpleLog object) {
      updateLog(object);
    }
  };

  StatusView statusView = new StatusView(new StatusPresenter.Listener() {
    @Override
    public void onDisplayType(DisplayType type) {
      if (MainPresenter.this.displayType == type) return;
      MainPresenter.this.displayType = type;
      displayLogs();
    }
  });

  //endregion


}
