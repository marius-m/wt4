package lt.markmerkk.utils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import lt.markmerkk.Translation;

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
        MenuItem updateItem = new MenuItem(Translation.getInstance().getString("general_update"),
            new ImageView(new Image(getClass().getResource("/update_2.png").toString())));
        updateItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (table.getSelectionModel().getSelectedItem() != null && listener != null)
                    listener.onUpdate(table.getSelectionModel().getSelectedItem());
            }
        });
        MenuItem deleteItem = new MenuItem(Translation.getInstance().getString("general_delete"),
            new ImageView(new Image(getClass().getResource("/delete_2.png").toString())));
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (table.getSelectionModel().getSelectedItem() != null && listener != null)
                    listener.onDelete(table.getSelectionModel().getSelectedItem());
//                System.out.println("Delete "+table.getSelectionModel().getSelectedItem());
            }
        });
        MenuItem cloneItem = new MenuItem(Translation.getInstance().getString("general_clone"),
            new ImageView(new Image(getClass().getResource("/clone_2.png").toString())));
        cloneItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (table.getSelectionModel().getSelectedItem() != null && listener != null)
                    listener.onClone(table.getSelectionModel().getSelectedItem());
            }
        });
        contextMenu.getItems().addAll(updateItem, deleteItem, cloneItem);
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
        firstNameCol.prefWidthProperty().bind(table.widthProperty().divide(widthDivide).subtract(1));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Type, String>(property));
        return firstNameCol;
    }

    /**
     * Helper listener for execution actions on entities from the tableview
     * @param <Type>
     */
    public interface Listener<Type> {
        /**
         * Update action
         * @param object
         */
        void onUpdate(Type object);

        /**
         * Delete action
         * @param object
         */
        void onDelete(Type object);

        /**
         * Clone action
         * @param object
         */
        void onClone(Type object);
    }

}