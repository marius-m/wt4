package lt.markmerkk.export.entities

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import lt.markmerkk.entities.Log
import lt.markmerkk.utils.LogFormatters
import org.joda.time.LocalDate

class ExportWorklogViewModel(
        val log: Log,
        val includeDate: Boolean
) {
    val ticket: String = log.code.code
    val comment: String = log.comment
    val duration: String = LogFormatters.humanReadableDurationShort(log.time.duration)
    val date: String = log.time.start.toLocalDate().toString(LogFormatters.DATE_SHORT_FORMAT)
    val selected: Boolean = true

    val logAsStringProperty = SimpleStringProperty(this, "logAsString", LogFormatters.formatLogBasic(log, includeDate))
    val selectedProperty = SimpleBooleanProperty(this, "selected", selected)
    val ticketProperty = SimpleStringProperty(this, "name", ticket)
    val commentProperty = SimpleStringProperty(this, "comment", comment)
    val durationProperty = SimpleStringProperty(this, "duration", duration)
    val dateProperty = SimpleStringProperty(this, "date", date)
}