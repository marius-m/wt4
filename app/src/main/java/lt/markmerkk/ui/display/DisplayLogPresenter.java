package lt.markmerkk.ui.display;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import lt.markmerkk.IDataListener;
import lt.markmerkk.Main;
import lt.markmerkk.Translation;
import lt.markmerkk.entities.BasicLogStorage;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.listeners.IPresenter;
import lt.markmerkk.ui.interfaces.UpdateListener;
import lt.markmerkk.utils.LogDisplayController;
import lt.markmerkk.utils.TableDisplayController;
import lt.markmerkk.utils.tracker.SimpleTracker;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class DisplayLogPresenter implements Initializable, IPresenter, IDataListener<SimpleLog> {
    @Inject
    BasicLogStorage storage;
    @FXML
    TableView<SimpleLog> tableView;

    ObservableList<SimpleLog> logs;
    UpdateListener updateListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.getComponent().presenterComponent().inject(this);
        logs = FXCollections.observableArrayList(storage.getDataAsList());
        SimpleTracker.getInstance().getTracker().sendView(SimpleTracker.VIEW_DAY);
        tableView.setTooltip(new Tooltip(Translation.getInstance().getString("daylog_tooltip_title")));
        LogDisplayController logDisplayController =
                new LogDisplayController(tableView, logs, new TableDisplayController.Listener<SimpleLog>() {
                    @Override
                    public void onUpdate(SimpleLog object) {
                        if (updateListener == null) return;
                        updateListener.onUpdate(object);
                    }

                    @Override
                    public void onDelete(SimpleLog object) {
                        if (updateListener == null) return;
                        updateListener.onDelete(object);
                    }

                    @Override
                    public void onClone(SimpleLog object) {
                        if (updateListener == null) return;
                        updateListener.onClone(object);
                    }
                });
        storage.register(this);
    }

    @PreDestroy
    public void destroy() {
        storage.unregister(this);
    }


    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void onDataChange(@NotNull ObservableList<SimpleLog> data) {
        logs.clear();
        logs.addAll(data);
    }

    //region Classes

    //endregion

}
