package lt.markmerkk.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.inject.Inject;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.listeners.IPresenter;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.clock.ClockPresenter;
import lt.markmerkk.ui.clock.ClockView;
import lt.markmerkk.ui.display.DisplayLogView;
import lt.markmerkk.ui.interfaces.DialogListener;
import lt.markmerkk.ui.interfaces.UpdateListener;
import lt.markmerkk.ui.settings.SettingsView;
import lt.markmerkk.ui.status.StatusPresenter;
import lt.markmerkk.ui.status.StatusView;
import lt.markmerkk.ui.taskweb.TaskWebView;
import lt.markmerkk.ui.update.UpdateLogView;
import lt.markmerkk.ui.utils.DisplayType;
import lt.markmerkk.ui.week.WeekView;
import lt.markmerkk.utils.HiddenTabsController;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the main presenter of the app
 */
public class MainPresenter implements Initializable {
  @Inject BasicLogStorage storage;

  @FXML TabPane tabPane;
  @FXML BorderPane northPane;
  @FXML BorderPane southPane;

  IPresenter displayPresenter;

  Stage stage;
  Stage updateDialog;
  HiddenTabsController tabsController;

  public MainPresenter() {
    tabsController = new HiddenTabsController();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
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
    if (displayPresenter instanceof Destroyable)
      ((Destroyable) displayPresenter).destroy();
    displayPresenter = null;
    switch (storage.getDisplayType()) {
      case DAY:
        DisplayLogView simpleLogView = new DisplayLogView(updateListener);
        southPane.setCenter(simpleLogView.getView());
        displayPresenter = (IPresenter) simpleLogView.getPresenter();
        break;
      case WEEK:
        WeekView weekView = new WeekView(updateListener);
        southPane.setCenter(weekView.getView());
        displayPresenter = (IPresenter) weekView.getPresenter();
        break;
    }
  }

  /**
   * Displays update log window
   */
  private void updateLog(SimpleLog simpleLog) {
    UpdateLogView updateLogView = new UpdateLogView(updateWindowDialogListener, simpleLog);
    updateDialog = new Stage(StageStyle.TRANSPARENT);
    updateDialog.initModality(Modality.WINDOW_MODAL);
    updateDialog.initOwner(stage);
    // Need to adjust position
    // Need buttons to disable views
    Scene updateScene = new Scene(updateLogView.getView(), 450, 300);
    updateDialog.setScene(updateScene);
    updateDialog.setX(stage.getX() + stage.getWidth() / 2 - updateScene.getWidth() / 2);
    updateDialog.setY(stage.getY() + stage.getHeight() / 2 - updateScene.getHeight() / 2);
    updateDialog.show();
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

  DialogListener updateWindowDialogListener = new DialogListener() {
    @Override public void onSave() {
      if (updateDialog.isShowing())
        updateDialog.hide();
      displayLogs();
    }

    @Override
    public void onCancel() {
      if (updateDialog.isShowing())
        updateDialog.hide();
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
      displayLogs();
    }
  });

  //endregion


}
