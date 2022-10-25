package lt.markmerkk.widgets.export.tableview

import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.widgets.export.ExportWorklogItemModel
import tornadofx.*

class ImportTableCell: TableCellFragment<ExportWorklogViewModel, Boolean>() {

    private val itemModel = ExportWorklogItemModel().bindToRowItem(this)

    override val root = hbox(spacing = 4.0) {
        checkbox(property = itemModel.selected)
    }
}