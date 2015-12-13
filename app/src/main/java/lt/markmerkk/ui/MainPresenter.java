package lt.markmerkk.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import lt.markmerkk.ui.clock.ClockView;
import lt.markmerkk.ui.display.DisplayLogPresenter;
import lt.markmerkk.ui.display.DisplayLogView;
import lt.markmerkk.ui.update.UpdateLogPresenter;
import lt.markmerkk.ui.update.UpdateLogView;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the main presenter of the app
 */
public class MainPresenter implements Initializable {

  @FXML BorderPane northPane;
  @FXML BorderPane southPane;

  @Override public void initialize(URL location, ResourceBundle resources) {
    ClockView clockView = new ClockView();
    northPane.setCenter(clockView.getView());

    displayLogs();
  }

  //region Convenience

  /**
   * Displays all the logs
   */
  private void displayLogs() {
    DisplayLogView simpleLogView = new DisplayLogView();
    ((DisplayLogPresenter)simpleLogView.getPresenter()).setListener(
        displayListener);
    southPane.setCenter(simpleLogView.getView());
  }

  /**
   * Displays update log window
   */
  private void displayUpdateLog() {
    UpdateLogView updateLogView = new UpdateLogView();
    ((UpdateLogPresenter) updateLogView.getPresenter()).setListener(
        updateListener);
    southPane.setCenter(updateLogView.getView());
  }

  //endregion

  //region Listeners

  UpdateLogPresenter.Listener updateListener = new UpdateLogPresenter.Listener() {
    @Override public void onFinish() {
      displayLogs();
    }
  };

  DisplayLogPresenter.Listener displayListener = new DisplayLogPresenter.Listener() {
    @Override public void onUpdate(Object object) {
      displayUpdateLog();
    }
  };

  //endregion


}
