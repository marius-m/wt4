package lt.markmerkk.ui.display;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javax.inject.Inject;

import lt.markmerkk.Main;
import lt.markmerkk.Translation;
import lt.markmerkk.listeners.IPresenter;
import lt.markmerkk.entities.IDataStorage;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.ui.interfaces.UpdateListener;
import lt.markmerkk.utils.LogDisplayController;
import lt.markmerkk.utils.TableDisplayController;
import lt.markmerkk.utils.tracker.SimpleTracker;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class DisplayLogPresenter implements Initializable, IPresenter {
  @Inject
  IDataStorage<SimpleLog> storage;
  @FXML
  TableView<SimpleLog> tableView;

  UpdateListener updateListener;

  @Override public void initialize(URL location, ResourceBundle resources) {
      Main.getComponent().presenterComponent().inject(this);
    SimpleTracker.getInstance().getTracker().sendView(SimpleTracker.VIEW_DAY);
    tableView.setTooltip(new Tooltip(Translation.getInstance().getString("daylog_tooltip_title")));
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
