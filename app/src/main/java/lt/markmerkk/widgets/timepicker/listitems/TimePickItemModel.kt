package lt.markmerkk.widgets.timepicker.listitems

import tornadofx.ItemViewModel

class TimePickItemModel : ItemViewModel<TimePickViewModel>() {
    val time = bind(TimePickViewModel::timeAsProperty)
}