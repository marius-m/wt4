package lt.markmerkk.utils;

import com.google.common.eventbus.EventBus;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import lt.markmerkk.Translation;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.entities.SyncStatus;
import lt.markmerkk.ui_2.views.ContextMenuEditLog;

/**
 * Created by mariusm on 1/19/15.
 * Responsible for displaying {@link SimpleLog} in the
 * {@link TableView}
 */
public class LogDisplayControllerSimple extends TableDisplayController {

    public LogDisplayControllerSimple(
            TableView table,
            ObservableList<SimpleLog> items,
            ContextMenuEditLog contextMenu,
            EventBus eventBus
    ) {
        super(table, items, contextMenu, eventBus);
    }

    @Override
    protected void init() {
        table.getColumns().add(insertImageColumn(26));
        table.getColumns().add(insertTableColumn(
                Translation.getInstance().getString("general_task"),
                "task",
                7
        ));
        table.getColumns().add(insertTableColumn(
            Translation.getInstance().getString("general_comment"),
                "comment",
                1.3f
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
              SimpleLog simpleLog = param.getValue();
              SyncStatus syncStatus = SyncStatus.exposeStatus(simpleLog);
            return new SimpleObjectProperty<ImageView>(new ImageView(SyncStatus.toStatusImageUrl(syncStatus)));
          }
        });
    return columnImage;
  }

}
