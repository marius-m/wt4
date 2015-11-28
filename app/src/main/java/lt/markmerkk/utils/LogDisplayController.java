package lt.markmerkk.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import lt.markmerkk.storage2.SimpleLog;

/**
 * Created by mariusm on 1/19/15.
 * Responsible for displaying {@link SimpleLog} in the
 * {@link TableView}
 */
public class LogDisplayController extends TableDisplayController<SimpleLog> {

    public LogDisplayController(TableView table, ObservableList<SimpleLog> items, Listener listener) {
        super(table, items, listener);
    }

    @Override
    protected void init() {
        //table.getColumns().add(insertTableColumn(
        //        ".",
        //        "storeLocation",
        //        20
        //));
        table.getColumns().add(insertTableColumn(
                "Task",
                "task",
                7
        ));
        table.getColumns().add(insertTableColumn(
                "Start",
                "shortStart",
                7
        ));

        table.getColumns().add(insertTableColumn(
                "End",
                "shortEnd",
                6
        ));
        table.getColumns().add(insertTableColumn(
                "Duration",
                "prettyDuration",
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
