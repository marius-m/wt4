package lt.markmerkk.ui.status;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.ILoggerListener;
import lt.markmerkk.utils.UserSettings;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, Destroyable {
  @Inject
  BasicLogStorage storage;
  @FXML
  TextField outputStatus;

  String total;
  String lastUpdate;

  @Override public void initialize(URL location, ResourceBundle resources) {
    storage.register(loggerListener);
    total = storage.getTotal();
    updateStatus();
  }

  @Override public void destroy() {
    storage.unregister(loggerListener);
  }

  /**
   * Convenience method to update current status
   */
  void updateStatus() {
    // fixme : incomplete
    outputStatus.setText(String.format("%s / Update: never", total));
  }

  ILoggerListener loggerListener = new ILoggerListener() {
    @Override
    public void onDataChange(ObservableList data) {
      total = storage.getTotal();
      updateStatus();
    }
  };

}
