package lt.markmerkk.utils;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lt.markmerkk.Tags;
import lt.markmerkk.WTEventBus;
import lt.markmerkk.entities.LogEditType;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.events.EventEditLog;
import lt.markmerkk.ui_2.views.ContextMenuEditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Sets up table and binds it to the TableView
 * Lifecycle events: {@link #onAttach()} {@link #onDetach()}
 */
public abstract class TableDisplayController {

    private static final Logger logger = LoggerFactory.getLogger(Tags.INTERNAL);

    protected TableView table;
    protected ObservableList<SimpleLog> items;
    private ContextMenuEditLog contextMenu;
    private WTEventBus eventBus;

    public TableDisplayController(
            TableView table,
            ObservableList<SimpleLog> items,
            ContextMenuEditLog contextMenu,
            WTEventBus eventBus
    ) {
        this.table = table;
        this.items = items;
        this.contextMenu = contextMenu;
        this.eventBus = eventBus;
        table.getColumns().clear();
        table.setEditable(false);
        table.setContextMenu(contextMenu.getRoot());
        table.setRowFactory(tv -> {
            TableRow<SimpleLog> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    eventBus.post(
                            new EventEditLog(
                                    LogEditType.UPDATE,
                                    (SimpleLog) row.getItem()
                            )
                    );
                }
            });
            return row;
        });
        init();
        table.setItems(items);
    }

    public void onAttach() {
        table.getSelectionModel().getSelectedItems().addListener(listChangeListener);
    }

    public void onDetach() {
        table.getSelectionModel().getSelectedItems().removeListener(listChangeListener);
    }

    protected abstract void init();

    protected TableColumn insertTableColumn(String caption, String property, float widthDivide) {
        TableColumn firstNameCol = new TableColumn(caption);
        firstNameCol.prefWidthProperty().bind(table.widthProperty().divide(widthDivide).subtract(1));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<SimpleLog, String>(property));
        return firstNameCol;
    }

    private ListChangeListener<SimpleLog> listChangeListener = new ListChangeListener<SimpleLog>() {
        @Override
        public void onChanged(Change change) {
            if (change.next()) {
                final List<SimpleLog> selectedItems = change.getList();
                if (selectedItems.size() > 0) {
                    final SimpleLog selectedLog = selectedItems.get(0);
                    contextMenu.bindLog(selectedLog.get_id());
                }
            }
        }
    };


}