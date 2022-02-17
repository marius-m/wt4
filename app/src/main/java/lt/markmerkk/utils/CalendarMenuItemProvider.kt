package lt.markmerkk.utils

import javafx.scene.control.MenuItem
import lt.markmerkk.LogRepository
import lt.markmerkk.Strings
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.cloneAsNewLocal
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.TicketCode
import java.time.ZonedDateTime

object CalendarMenuItemProvider {

    @JvmStatic
    fun provideMenuItemNewItem(
        zonedDateTime: ZonedDateTime,
        strings: Strings,
        timeProvider: TimeProvider,
        logRepository: LogRepository
    ): MenuItem {
        val newEntryItem = MenuItem(strings.getString("calendar_menu_new_entry"))
        newEntryItem.setOnAction {
            val startMillis = zonedDateTime.toInstant().toEpochMilli()
            val endMillis = zonedDateTime.plusHours(1).toInstant().toEpochMilli()
            val log = Log.createAsEmpty(timeProvider = timeProvider)
                .cloneAsNewLocal(
                    timeProvider = timeProvider,
                    start = timeProvider.roundDateTime(startMillis),
                    end = timeProvider.roundDateTime(endMillis)
                )
            logRepository.insertOrUpdate(log)
        }
        return newEntryItem
    }
}