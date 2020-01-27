package lt.markmerkk.widgets.tickets

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty

class TicketStatusViewModel(
        name: String,
        enabled: Boolean
) {
    val nameProperty = SimpleStringProperty(this, "name", name)
    val enableProperty = SimpleBooleanProperty(this, "enabled", enabled)
}