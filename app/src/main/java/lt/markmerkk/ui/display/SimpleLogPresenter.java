package lt.markmerkk.ui.display;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javax.inject.Inject;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.utils.LogDisplayController;
import lt.markmerkk.utils.TableDisplayController;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class SimpleLogPresenter implements Initializable {
  @Inject BasicLogStorage storage;
  @FXML TableView<SimpleLog> tableView;

  @Override public void initialize(URL location, ResourceBundle resources) {
    LogDisplayController logDisplayController =
        new LogDisplayController(tableView, storage.getData(), new TableDisplayController.Listener() {
          @Override public void onUpdate(Object object) { }

          @Override public void onDelete(Object object) {
            storage.delete((SimpleLog)object);
          }

          @Override public void onClone(Object object) {
            storage.insert((SimpleLog)object);
          }
        });
  }

}
