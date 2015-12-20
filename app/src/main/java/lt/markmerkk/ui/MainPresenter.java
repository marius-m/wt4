package lt.markmerkk.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.clock.ClockPresenter;
import lt.markmerkk.ui.clock.ClockView;
import lt.markmerkk.ui.display.DisplayLogPresenter;
import lt.markmerkk.ui.display.DisplayLogView;
import lt.markmerkk.ui.settings.SettingsPresenter;
import lt.markmerkk.ui.settings.SettingsView;
import lt.markmerkk.ui.update.UpdateLogPresenter;
import lt.markmerkk.ui.update.UpdateLogView;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the main presenter of the app
 */
public class MainPresenter implements Initializable {

  @FXML TabPane tabPane;
  @FXML BorderPane northPane;
  @FXML BorderPane southPane;

  @Override public void initialize(URL location, ResourceBundle resources) {
    ClockView clockView = new ClockView(clockListener);
    northPane.setCenter(clockView.getView());

    displayLogs();
    modifyTabShowing();
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

  /**
   * Modifies tab showing depending on displayed tabs.
   * Uses a dirty hack to display tabs only when there are more than one tag.
   * Source: https://gist.github.com/twasyl/7fc08b5843964823e36b
   */
  private void modifyTabShowing() {
    Platform.runLater(() -> {
      tabPane.getTabs().addListener((ListChangeListener) change -> {
        final StackPane header = (StackPane) tabPane.lookup(".tab-header-area");
        if (header != null) {
          if (this.tabPane.getTabs().size() == 1) header.setPrefHeight(0);
          else header.setPrefHeight(-1);
        }
      });
      Tab mockTab = new Tab();
      tabPane.getTabs().add(mockTab);
      tabPane.getTabs().remove(mockTab);
    });
  }

  //endregion

  //region Listeners

  ClockPresenter.Listener clockListener = new ClockPresenter.Listener() {
    @Override public void onNew() {
      Tab mockTab = new Tab("Settings");
      final SettingsView settingsView = new SettingsView();
      mockTab.setContent(settingsView.getView());
      tabPane.getTabs().add(mockTab);
      tabPane.getSelectionModel().select(mockTab);
      mockTab.setOnClosed(new EventHandler<Event>() {
        @Override public void handle(Event event) {
          if (settingsView.getPresenter() instanceof Destroyable)
            ((Destroyable) settingsView.getPresenter()).destroy();
        }
      });
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
