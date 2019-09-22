package lt.markmerkk.utils

import javafx.scene.control.MenuItem
import lt.markmerkk.LogStorage
import lt.markmerkk.Strings
import lt.markmerkk.entities.SimpleLogBuilder
import java.time.ZonedDateTime

object CalendarMenuItemProvider {

    @JvmStatic
    fun provideMenuItemNewItem(
            zonedDateTime: ZonedDateTime,
            strings: Strings,
            logStorage: LogStorage
    ): MenuItem {
        val newEntryItem = MenuItem(strings.getString("calendar_menu_new_entry"))
        newEntryItem.setOnAction {
            val startMillis = zonedDateTime.toInstant().toEpochMilli()
            val endMillis = zonedDateTime.plusHours(1).toInstant().toEpochMilli()
            val simpleLog = SimpleLogBuilder()
                    .setStart(startMillis)
                    .setEnd(endMillis)
                    .build()
            logStorage.insert(simpleLog)
        }
        return newEntryItem
    }
}