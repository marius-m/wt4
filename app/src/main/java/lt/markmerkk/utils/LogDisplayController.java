package lt.markmerkk.utils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
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
        table.getColumns().add(insertImageColumn(26));
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
    }

  protected TableColumn<SimpleLog, ImageView> insertImageColumn(float constant) {
    TableColumn<SimpleLog, ImageView> columnImage = new TableColumn<SimpleLog,ImageView>(".");
    //columnImage.prefWidthProperty().bind(table.widthProperty().divide(widthDivide).subtract(1));
    columnImage.prefWidthProperty().bind(new SimpleDoubleProperty(constant));
    columnImage.setCellValueFactory(
        new Callback<TableColumn.CellDataFeatures<SimpleLog, ImageView>, ObservableValue<ImageView>>() {
          @Override public ObservableValue<ImageView> call(
              TableColumn.CellDataFeatures<SimpleLog, ImageView> param) {
            return new SimpleObjectProperty<ImageView>(new ImageView(param.getValue().getStateImageUrl()));
          }
        });
    return columnImage;
  }

}
