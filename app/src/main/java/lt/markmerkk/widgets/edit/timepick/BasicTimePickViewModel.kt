package lt.markmerkk.widgets.edit.timepick

import javafx.beans.property.SimpleStringProperty
import lt.markmerkk.utils.LogFormatters
import org.joda.time.LocalTime

class BasicTimePickViewModel(
    val time: LocalTime
) {
    val timeAsString: String = LogFormatters.formatTime.print(time)
    val timeAsStringProperty = SimpleStringProperty(this, "time", timeAsString)
}