package lt.markmerkk.ui;

import com.airhacks.afterburner.views.FXMLView;
import com.brsanthu.googleanalytics.PageViewHit;
import com.vinumeris.updatefx.UpdateSummary;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.inject.Inject;
import lt.markmerkk.Main;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.listeners.IPresenter;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
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
import lt.markmerkk.ui.version.VersionView;
import lt.markmerkk.ui.week.WeekView;
import lt.markmerkk.utils.HiddenTabsController;
import lt.markmerkk.utils.VersionController;
import lt.markmerkk.utils.tracker.SimpleTracker;

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
  Stage dialog;
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
      if (dialog.isShowing())
        dialog.hide();
      storage.notifyDataChange();
    }

    @Override
    public void onCancel() {
      if (dialog.isShowing())
        dialog.hide();
    }
  };

  DialogListener versionWindowDialogListener = new DialogListener() {
    @Override public void onSave() { }

    @Override
    public void onCancel() {
      if (dialog.isShowing())
        dialog.hide();
    }
  };

  UpdateListener updateListener = new UpdateListener() {
    @Override public void onUpdate(SimpleLog object) {
      UpdateLogView updateLogView = new UpdateLogView(updateWindowDialogListener, object);
      openDialog(updateLogView);
    }

    @Override
    public void onDelete(SimpleLog object) {
      storage.delete(object);
    }

    @Override
    public void onClone(SimpleLog object) {
      SimpleLog newLog = new SimpleLogBuilder()
          .setStart(object.getStart())
          .setEnd(object.getEnd())
          .setTask(object.getTask())
          .setComment(object.getComment())
          .build();
      storage.insert(newLog);
    }
  };

  StatusView statusView = new StatusView(new StatusPresenter.Listener() {
    @Override
    public void onDisplayType(DisplayType type) {
      displayLogs();
    }

    @Override
    public void onAbout() {
      VersionView versionView = new VersionView(versionWindowDialogListener);
      openDialog(versionView);
    }
  });

  //endregion

  //region Convenience

  /**
   * Opens dialog view
   */
  private void openDialog(FXMLView view) {
    if (view == null) return;
    dialog = new Stage(StageStyle.TRANSPARENT);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(stage);
    // Need to adjust position
    // Need buttons to disable views
    Scene updateScene = new Scene(view.getView(), 450, 300);
    dialog.setScene(updateScene);
    dialog.setX(stage.getX() + stage.getWidth() / 2 - updateScene.getWidth() / 2);
    dialog.setY(stage.getY() + stage.getHeight() / 2 - updateScene.getHeight() / 2);
    dialog.show();
  }

  //endregion

}
