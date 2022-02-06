package lt.markmerkk.widgets.datetimepicker.listitems

import tornadofx.ItemViewModel

class TimePickItemModel : ItemViewModel<TimePickViewModel>() {
    val time = bind(TimePickViewModel::timeAsProperty)
}