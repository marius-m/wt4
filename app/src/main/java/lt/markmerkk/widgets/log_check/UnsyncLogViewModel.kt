package lt.markmerkk.widgets.log_check

import javafx.beans.property.SimpleStringProperty
import lt.markmerkk.utils.LogFormatters
import org.joda.time.LocalDate

class UnsyncLogViewModel(
        val ticket: String,
        val comment: String,
        val duration: String,
        val date: LocalDate
) {
    val ticketProperty = SimpleStringProperty(this, "name", ticket)
    val commentProperty = SimpleStringProperty(this, "comment", comment)
    val durationProperty = SimpleStringProperty(this, "duration", duration)
    val dateProperty = SimpleStringProperty(this, "date", date.toString(LogFormatters.DATE_SHORT_FORMAT))
}