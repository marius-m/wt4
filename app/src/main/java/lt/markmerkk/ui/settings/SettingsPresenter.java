package lt.markmerkk.ui.settings;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import lt.markmerkk.listeners.Destroyable;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to edit settings
 */
public class SettingsPresenter implements Initializable, Destroyable {

  @Override public void initialize(URL location, ResourceBundle resources) {
    System.out.println("Hello world!");
  }

  @Override public void destroy() {
    System.out.println("Bye!");
  }
}
