package lt.markmerkk.widgets.export

import lt.markmerkk.export.entities.ExportWorklogViewModel
import tornadofx.*

class ExportWorklogItemFragment : ListCellFragment<ExportWorklogViewModel>() {
    private val itemModel = ExportWorklogItemModel().bindTo(this)

    override val root = hbox(spacing = 4.0) {
        checkbox(property = itemModel.selected)
        label {
            textProperty().bind(itemModel.logAsString)
        }
    }
}