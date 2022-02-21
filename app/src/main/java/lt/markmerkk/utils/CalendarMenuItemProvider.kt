package lt.markmerkk.utils

import javafx.scene.control.MenuItem
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Strings
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.cloneAsNewLocal
import java.time.ZonedDateTime

object CalendarMenuItemProvider {

    @JvmStatic
    fun provideMenuItemNewItem(
        zonedDateTime: ZonedDateTime,
        strings: Strings,
        timeProvider: TimeProvider,
        activeDisplayRepository: ActiveDisplayRepository
    ): MenuItem {
        val newEntryItem = MenuItem(strings.getString("calendar_menu_new_entry"))
        newEntryItem.setOnAction {
            val startMillis = zonedDateTime.toInstant().toEpochMilli()
            val endMillis = zonedDateTime.plusHours(1).toInstant().toEpochMilli()
            val log = Log.createAsEmpty(timeProvider = timeProvider)
                .cloneAsNewLocal(
                    timeProvider = timeProvider,
                    start = timeProvider.roundMillisToDt(startMillis),
                    end = timeProvider.roundMillisToDt(endMillis)
                )
            activeDisplayRepository.insertOrUpdate(log)
        }
        return newEntryItem
    }
}