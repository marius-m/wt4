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
public class DisplayLogPresenter implements Initializable {
  @Inject BasicLogStorage storage;
  @FXML TableView<SimpleLog> tableView;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    LogDisplayController logDisplayController =
        new LogDisplayController(tableView, storage.getData(), new TableDisplayController.Listener<SimpleLog>() {
          @Override public void onUpdate(SimpleLog object) {
            if (listener == null) return;
            listener.onUpdate(object);
          }

          @Override public void onDelete(SimpleLog object) {
            storage.delete(object);
          }

          @Override public void onClone(SimpleLog object) {
            storage.insert(object);
          }
        });
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  //region Classes

  /**
   * Helper listener for the log display
   */
  public interface Listener {
    /**
     * Called whenever items is being updated
     * @param object item set for update
     */
    void onUpdate(SimpleLog object);
  }

  //endregion

}
