package lt.markmerkk.widgets.edit.timepick

import tornadofx.*

class BasicTimePickItemModel: ItemViewModel<BasicTimePickViewModel>() {
    val timeObservable = bind(BasicTimePickViewModel::timeAsStringProperty)
}