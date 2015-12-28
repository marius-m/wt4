package lt.markmerkk.ui.settings;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.utils.AdvHashSettings;
import lt.markmerkk.utils.UserSettings;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to edit settings
 */
public class SettingsPresenter implements Initializable, Destroyable {

  @Inject
  UserSettings settings;

  @FXML
  TextField inputHost, inputUsername;
  @FXML
  PasswordField inputPassword;
  @FXML
  TextArea outputLogger;


  @Override public void initialize(URL location, ResourceBundle resources) {
    inputHost.setText(settings.getHost());
    inputUsername.setText(settings.getUsername());
    inputPassword.setText(settings.getPassword());
  }

  @Override public void destroy() {
    settings.setHost(inputHost.getText());
    settings.setUsername(inputUsername.getText());
    settings.setPassword(inputPassword.getText());
  }
}
