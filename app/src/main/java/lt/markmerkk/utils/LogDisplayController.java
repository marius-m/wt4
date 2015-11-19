package lt.markmerkk.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import lt.markmerkk.storage.entities.table.LogTable;

/**
 * Created by mariusm on 1/19/15.
 */
public class LogDisplayController extends TableDisplayController<LogTable> {

    public LogDisplayController(TableView table, ObservableList<LogTable> items, Listener listener) {
        super(table, items, listener);
    }

    @Override
    protected void init() {
        table.getColumns().add(insertTableColumn(
                ".",
                "storeLocation",
                20
        ));
        table.getColumns().add(insertTableColumn(
                "Task",
                "category",
                7
        ));
        table.getColumns().add(insertTableColumn(
                "Start",
                "verbalStart",
                7
        ));

        table.getColumns().add(insertTableColumn(
                "End",
                "verbalEnd",
                6
        ));
        table.getColumns().add(insertTableColumn(
                "Duration",
                "verbalDuration",
                6
        ));
        table.getColumns().add(insertTableColumn(
                "Comment",
                "comment",
                3
        ));
//        table.getColumns().add(insertTableColumn(
//                ".",
//                "gitMessageIndicator",
//                20
//        ));

    }
}
