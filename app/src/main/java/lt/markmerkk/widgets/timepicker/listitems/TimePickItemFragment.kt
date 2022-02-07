package lt.markmerkk.widgets.timepicker.listitems

import tornadofx.ListCellFragment
import tornadofx.bindTo
import tornadofx.hbox
import tornadofx.label

class TimePickItemFragment : ListCellFragment<TimePickViewModel>() {
    private val itemModel = TimePickItemModel().bindTo(this)

    override val root = hbox(spacing = 4.0) {
        label {
            textProperty().bind(itemModel.time)
        }
    }
}