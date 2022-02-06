package lt.markmerkk.widgets.datetimepicker.listitems

import javafx.beans.property.SimpleStringProperty

class TimePickViewModel(
    timeAsString: String
) {
    val timeAsProperty = SimpleStringProperty(this, "time", timeAsString)
}