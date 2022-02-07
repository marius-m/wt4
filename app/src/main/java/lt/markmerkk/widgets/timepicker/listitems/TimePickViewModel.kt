package lt.markmerkk.widgets.timepicker.listitems

import javafx.beans.property.SimpleStringProperty

class TimePickViewModel(
    timeAsString: String
) {
    val timeAsProperty = SimpleStringProperty(this, "time", timeAsString)
}