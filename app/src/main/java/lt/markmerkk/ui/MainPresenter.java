package lt.markmerkk.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import lt.markmerkk.ui.clock.ClockView;
import lt.markmerkk.ui.display.SimpleLogView;

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

    SimpleLogView simpleLogView = new SimpleLogView();
    southPane.setCenter(simpleLogView.getView());
  }
}
