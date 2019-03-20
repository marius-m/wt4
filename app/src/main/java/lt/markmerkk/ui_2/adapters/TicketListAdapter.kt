package lt.markmerkk.ui_2.adapters

import com.jfoenix.controls.*
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.control.PopupControl.USE_COMPUTED_SIZE
import javafx.scene.layout.StackPane
import javafx.util.Callback
import lt.markmerkk.Tags
import lt.markmerkk.entities.Ticket
import org.slf4j.LoggerFactory

class TicketListAdapter(
        private val jfxDialogLayout: JFXDialogLayout,
        private val jfxTable: JFXTreeTableView<TicketViewItem>
) {

    private val ticketViewItems: ObservableList<TicketViewItem>
            = FXCollections.observableArrayList<TicketViewItem>()

    init {
        val colCode = JFXTreeTableColumn<TicketViewItem, String>("Code")
        colCode.prefWidth = 100.0
        colCode.setCellValueFactory { param: TreeTableColumn.CellDataFeatures<TicketViewItem, String> ->
            param.value.value.propertyCode
        }
        colCode.isResizable = false
        val colDescription = JFXTreeTableColumn<TicketViewItem, String>("Description")
        colDescription.prefWidth = 200.0
        colDescription.setCellValueFactory { param -> param.value.value.propertyDescription }
        colDescription.isResizable = false
        val colPick = JFXTreeTableColumn<TicketViewItem, JFXButton>("P")
        colPick.prefWidth = 60.0
        colPick.isResizable = false
        colPick.cellFactory = object : Callback<TreeTableColumn<TicketViewItem, JFXButton>, TreeTableCell<TicketViewItem, JFXButton>> {
            override fun call(param: TreeTableColumn<TicketViewItem, JFXButton>): TreeTableCell<TicketViewItem, JFXButton> {
                return object : TreeTableCell<TicketViewItem, JFXButton>() {
                    val button = JFXButton("Test")
                    override fun updateItem(item: JFXButton?, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (empty) {
                            graphic = null
                        } else {
                            graphic = button
                        }
                    }
                }
            }
        }
        val scrollBarHorizontal = 14.0
        jfxTable.widthProperty().addListener { observable, oldValue, newValue ->
            colDescription.prefWidth = newValue.toDouble() - colCode.width - colPick.width - scrollBarHorizontal
        }
        val root: TreeItem<TicketViewItem> = RecursiveTreeItem<TicketViewItem>(
                ticketViewItems,
                { param -> param.children }
        )
        jfxTable.root = root
        jfxTable.isShowRoot = false
        jfxTable.isEditable = false
        jfxTable.columns.setAll(colCode, colDescription, colPick)
        jfxDialogLayout.widthProperty().addListener { observable, oldValue, newValue ->
            jfxTable.prefWidth = newValue.toDouble()
        }
        jfxDialogLayout.heightProperty().addListener { observable, oldValue, newValue ->
            jfxTable.prefHeight = newValue.toDouble()
        }
    }

    fun renewTickets(tickets: List<Ticket>) {
//        val ticketsAsString = tickets
//                .map { "${it.code.code}: ${it.description}" }
//                .map { Label(it) }
        val ticketAsItems = tickets.map { TicketViewItem(it, it.code.code, it.description) }
        ticketViewItems.clear()
        ticketViewItems.addAll(ticketAsItems)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }

}

data class TicketViewItem(
        private val ticket: Ticket,
        private val code: String,
        private val description: String
) : RecursiveTreeObject<TicketViewItem>() {
    val propertyCode: StringProperty = SimpleStringProperty(code)
    val propertyDescription: StringProperty = SimpleStringProperty(description)
}
