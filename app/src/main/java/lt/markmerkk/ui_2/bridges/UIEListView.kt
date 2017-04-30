package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXTreeTableColumn
import com.jfoenix.controls.JFXTreeTableView
import com.jfoenix.controls.RecursiveTreeItem
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeTableColumn

class UIEListView(
        private val listView: JFXTreeTableView<TreeLog>,
        private val firstColumn: JFXTreeTableColumn<TreeLog, String>
) : UIElement {

    private val logs: ObservableList<TreeLog> = FXCollections.observableArrayList()

    init {
        listView.root = RecursiveTreeItem<TreeLog>(
                logs,
                RecursiveTreeObject<TreeLog>::getChildren
        )
        firstColumn.setCellValueFactory({ param: TreeTableColumn.CellDataFeatures<TreeLog, String> ->
            if (firstColumn.validateValue(param)) {
                param.value.value.firstName
            } else {
                firstColumn.getComputedValue(param)
            }
        })

        listView.isShowRoot = false

        // todo : remove test data
        for (i in 0..10) {
            logs.add(TreeLog(
                    SimpleStringProperty("name" + i),
                    SimpleStringProperty("surname" + i),
                    SimpleIntegerProperty(i)
            ))
        }
    }

    override fun show() {
        throw UnsupportedOperationException()
    }

    override fun hide() {
        throw UnsupportedOperationException()
    }

    class TreeLog(
            val firstName: StringProperty = SimpleStringProperty(),
            val lastName: StringProperty = SimpleStringProperty(),
            val age: IntegerProperty = SimpleIntegerProperty()
    ) : RecursiveTreeObject<TreeLog>()

}