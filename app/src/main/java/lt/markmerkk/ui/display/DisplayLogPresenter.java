package lt.markmerkk.ui.display;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javax.inject.Inject;
import lt.markmerkk.listeners.IPresenter;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.ui.interfaces.UpdateListener;
import lt.markmerkk.utils.LogDisplayController;
import lt.markmerkk.utils.TableDisplayController;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class DisplayLogPresenter implements Initializable, IPresenter {
  @Inject BasicLogStorage storage;
  @FXML TableView<SimpleLog> tableView;

  UpdateListener updateListener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    tableView.setTooltip(new Tooltip("Worklog display" +
        "\n\nToday's current work log"));
    LogDisplayController logDisplayController =
        new LogDisplayController(tableView, storage.getData(), new TableDisplayController.Listener<SimpleLog>() {
          @Override public void onUpdate(SimpleLog object) {
            if (updateListener == null) return;
            updateListener.onUpdate(object);
          }

          @Override public void onDelete(SimpleLog object) {
            if (updateListener == null) return;
            updateListener.onDelete(object);
          }

          @Override public void onClone(SimpleLog object) {
            if (updateListener == null) return;
            updateListener.onClone(object);
          }
        });
  }

  public void setUpdateListener(UpdateListener updateListener) {
    this.updateListener = updateListener;
  }

  //region Classes

  //endregion

}
