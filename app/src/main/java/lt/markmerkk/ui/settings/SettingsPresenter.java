package lt.markmerkk.ui.settings;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.jira.WorkExecutor;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.utils.AdvHashSettings;
import lt.markmerkk.utils.UserSettings;
import lt.markmerkk.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to edit settings
 */
public class SettingsPresenter implements Initializable, Destroyable {

  @Inject
  UserSettings settings;

  @FXML TextField inputHost, inputUsername;
  @FXML PasswordField inputPassword;
  @FXML TextArea outputLogger;
  @FXML Button buttonRefresh;

  Appender guiAppender;

  @Override public void initialize(URL location, ResourceBundle resources) {
    inputHost.setText(settings.getHost());
    inputUsername.setText(settings.getUsername());
    inputPassword.setText(settings.getPassword());
    buttonRefresh.setOnMouseClicked(refreshClickListener);

    guiAppender = new AppenderSkeleton() {
      @Override
      public boolean requiresLayout() { return true; }

      @Override
      public void close() { }

      @Override
      protected void append(LoggingEvent event) {
        Platform.runLater(() -> outputLogger.appendText(layout.format(event)));
      }
    };
    guiAppender.setLayout(new PatternLayout("%d{ABSOLUTE} - %m%n"));
    outputLogger.clear();
    outputLogger.setText(Utils.lastLog());
    Logger.getRootLogger().addAppender(guiAppender);
  }

  @Override public void destroy() {
    Logger.getRootLogger().removeAppender(guiAppender);
    settings.setHost(inputHost.getText());
    settings.setUsername(inputUsername.getText());
    settings.setPassword(inputPassword.getText());
    guiAppender.close();
  }

  //region Listeners

  EventHandler<MouseEvent> refreshClickListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
    }
  };

  //endregion

}
