package lt.markmerkk.clock;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.RocketService;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter for the clock for logging info.
 */
public class ClockPresenter implements Initializable {

  @Inject RocketService rocket;

  @Override public void initialize(URL location, ResourceBundle resources) {
    System.out.println("Hello world");
    rocket.run();
  }

}
