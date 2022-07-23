package lt.markmerkk.widgets.edit.timepick

import tornadofx.*

class BasicTimePickItemFragment : ListCellFragment<BasicTimePickViewModel>() {
    private val itemModel = BasicTimePickItemModel().bindTo(this)

    override val root = hbox(spacing = 4.0) {
        label {
            textProperty().bind(itemModel.timeObservable)
        }
    }
}