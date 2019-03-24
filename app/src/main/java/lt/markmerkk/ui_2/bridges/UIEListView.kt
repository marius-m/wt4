package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXTreeTableColumn
import com.jfoenix.controls.JFXTreeTableView
import com.jfoenix.controls.RecursiveTreeItem
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeTableColumn
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui.UIElement
import lt.markmerkk.utils.LogUtils

class UIEListView(
        private val logStorage: LogStorage,
        private val listView: JFXTreeTableView<TreeLog>,
        private val columnDuration: JFXTreeTableColumn<TreeLog, String>,
        private val columnMessage: JFXTreeTableColumn<TreeLog, String>
) : UIElement<JFXTreeTableView<UIEListView.TreeLog>>, IDataListener<SimpleLog> {

    private val logs: ObservableList<TreeLog> = FXCollections.observableArrayList()
//    private val glyphEdit: SVGGlyph = glyphUpdate()

    init {
        listView.root = RecursiveTreeItem<TreeLog>(
                logs,
                RecursiveTreeObject<TreeLog>::getChildren
        )
        columnDuration.setCellValueFactory({
            param: TreeTableColumn.CellDataFeatures<TreeLog, String> ->
            if (columnDuration.validateValue(param)) {
                param.value.value.duration
            } else {
                columnDuration.getComputedValue(param)
            }
        })
        columnMessage.setCellValueFactory({
            param: TreeTableColumn.CellDataFeatures<TreeLog, String> ->
            if (columnMessage.validateValue(param)) {
                param.value.value.message
            } else {
                columnMessage.getComputedValue(param)
            }
        })
//        listView.setOnContextMenuRequested {
//            val contextListViewActions = TreeContextMenu().view as JFXPopup
//            contextListViewActions.source = listView
//            contextListViewActions.show(
//                    JFXPopup.PopupVPosition.TOP,
//                    JFXPopup.PopupHPosition.LEFT,
//                    it.x,
//                    it.y
//            )
//        }
        listView.isShowRoot = false
//        val testlog = SimpleLogBuilder()
//                .setStart(DateTime.now().minusHours(3).millis)
//                .setEnd(DateTime.now().millis)
//                .setTask("WT123")
//                .setComment("Lorem ipsum - tai fiktyvus tekstas naudojamas spaudos ir grafinio dizaino pasaulyje jau nuo XVI a. pradžios. Lorem Ipsum tapo standartiniu fiktyviu tekstu, kai nežinomas spaustuvininkas atsitiktine tvarka išdėliojo raides atspaudų prese ir tokiu būdu sukūrė raidžių egzempliorių. Šis tekstas išliko beveik nepasikeitęs ne tik penkis amžius, bet ir įžengė i kopiuterinio grafinio dizaino laikus. Jis išpopuliarėjo XX a. šeštajame dešimtmetyje, kai buvo išleisti Letraset lapai su Lorem Ipsum ištraukomis, o vėliau -leidybinė sistema AldusPageMaker, kurioje buvo ir Lorem Ipsum versija.")
//                .build()
//        onDataChange(listOf(
//                testlog,
//                testlog,
//                testlog
//        ))
        onDataChange(logStorage.data)
    }

    override fun raw(): JFXTreeTableView<TreeLog> = listView

    override fun show() {
        throw UnsupportedOperationException()
    }

    override fun hide() {
        throw UnsupportedOperationException()
    }

    override fun reset() {}

    override fun onDataChange(data: List<SimpleLog>) {
        logs.clear()
        data.forEach { logs.add(toTreeLog(it)) }
    }

    //region Convenience

    /**
     * Convenience function to for TreeLog from SimpleLog
     */
    private fun toTreeLog(simpleLog: SimpleLog): TreeLog {
        return TreeLog(
                SimpleStringProperty(simpleLog.task),
                SimpleStringProperty(LogUtils.formatDuration(simpleLog.duration)),
                SimpleStringProperty(simpleLog.comment)
        )
    }

    //endregion

    class TreeLog(
            val ticket: StringProperty = SimpleStringProperty(),
            val duration: StringProperty = SimpleStringProperty(),
            val message: StringProperty = SimpleStringProperty()
    ) : RecursiveTreeObject<TreeLog>()

}