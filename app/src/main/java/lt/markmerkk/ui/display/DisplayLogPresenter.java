package lt.markmerkk.ui.display;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javax.inject.Inject;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
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
    tableView.setTooltip(new Tooltip("Worklog display" +
        "\n\nToday's current work log"));
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
            SimpleLog newLog = new SimpleLogBuilder()
                .setStart(object.getStart())
                .setEnd(object.getEnd())
                .setTask(object.getTask())
                .setComment(object.getComment())
                .build();
            storage.insert(newLog);
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
