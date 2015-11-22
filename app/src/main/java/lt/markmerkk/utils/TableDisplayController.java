package lt.markmerkk.utils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lt.markmerkk.storage2.entities.SimpleLog;

/**
 * Created by mariusm on 11/6/14.
 */
public abstract class TableDisplayController<Type> {
    protected TableView table;
    protected ObservableList<Type> items;
    protected Listener listener;

    public TableDisplayController(TableView table, ObservableList<Type> items, Listener listener) {
        this.table = table;
        this.items = items;
        this.listener = listener;
        table.getColumns().clear();
        table.setEditable(false);

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem updateItem = new MenuItem("Update");
        updateItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (table.getSelectionModel().getSelectedItem() != null && listener != null)
                    listener.onUpdate(table.getSelectionModel().getSelectedItem());
            }
        });
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (table.getSelectionModel().getSelectedItem() != null && listener != null)
                    listener.onDelete(table.getSelectionModel().getSelectedItem());
//                System.out.println("Delete "+table.getSelectionModel().getSelectedItem());
            }
        });
        contextMenu.getItems().addAll(updateItem, deleteItem);
        table.setContextMenu(contextMenu);

        table.setRowFactory( tv -> {
            TableRow<Type> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    if (listener != null)
                        listener.onUpdate(row.getItem());
                }
            });
            return row ;
        });
        init();
        table.setItems(items);
    }

    protected abstract void init();

    protected TableColumn insertTableColumn(String caption, String property, float widthDivide) {
        TableColumn firstNameCol = new TableColumn(caption);
//        table.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        firstNameCol.prefWidthProperty().bind(table.widthProperty().divide(widthDivide).subtract(1));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Type, String>(property));
        return firstNameCol;
    }

    public interface Listener<Type> {
        public void onUpdate(Type object);
        public void onDelete(Type object);
    }

}