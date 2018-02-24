package lt.markmerkk.utils

import com.calendarfx.view.DayViewBase
import com.calendarfx.view.DetailedDayView
import com.calendarfx.view.DetailedWeekView
import javafx.scene.control.*
import lt.markmerkk.LogStorage
import lt.markmerkk.Strings
import lt.markmerkk.entities.SimpleLogBuilder
import java.time.ZonedDateTime

object CalendarMenuItemProvider {

    @JvmStatic fun provideMenuItemEditMode(
            strings: Strings,
            calendarEditRules: CalendarFxEditRules
    ): MenuItem {
        val menuItem = MenuItem(strings.getString("calendar_menu_edit_mode"))
        menuItem.setOnAction {
            calendarEditRules.enable()
        }
        return menuItem
    }

    @JvmStatic fun provideMenuItemNewItem(
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

    @JvmStatic fun provideMenuItemScale(dayView: DetailedDayView, strings: Strings): MenuItem {
        val hoursMenu = Menu(strings.getString("calendar_menu_hour_scale"))
        val hourHeight = MenuItem()
        val slider = Slider(40.0, 200.0, 50.0)
        slider.prefWidth = 100.0
        slider.value = dayView.hourHeight
        slider.valueProperty().addListener { it ->
            dayView.hoursLayoutStrategy = DayViewBase.HoursLayoutStrategy.FIXED_HOUR_HEIGHT
            dayView.hourHeight = slider.value
        }
        val sliderWrapper = Label()
        sliderWrapper.graphic = slider
        sliderWrapper.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        hourHeight.graphic = sliderWrapper
        hoursMenu.items.add(hourHeight)
        hoursMenu.items.add(SeparatorMenuItem())
        val hours = intArrayOf(4, 6, 8, 10, 12, 18, 24)
        for (h in hours) {
            val labelText = String.format(strings.getString("calendar_menu_hours"), h)
            val wrapper = Label(labelText)
            val item = MenuItem()
            item.graphic = wrapper
            item.setOnAction { evt ->
                dayView.earlyLateHoursStrategy = DayViewBase.EarlyLateHoursStrategy.SHOW
                dayView.hoursLayoutStrategy = DayViewBase.HoursLayoutStrategy.FIXED_HOUR_COUNT
                dayView.visibleHours = h
            }
            hoursMenu.items.add(item)
        }
        return hoursMenu
    }

    @JvmStatic fun provideMenuItemScale(weekView: DetailedWeekView, strings: Strings): MenuItem {
        val hoursMenu = Menu(strings.getString("calendar_menu_hour_scale"))
        val hourHeight = MenuItem()
        val slider = Slider(40.0, 200.0, 50.0)
        slider.prefWidth = 100.0
        slider.value = weekView.hourHeight
        slider.valueProperty().addListener { it ->
            weekView.hoursLayoutStrategy = DayViewBase.HoursLayoutStrategy.FIXED_HOUR_HEIGHT
            weekView.hourHeight = slider.value
        }
        val sliderWrapper = Label()
        sliderWrapper.graphic = slider
        sliderWrapper.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        hourHeight.graphic = sliderWrapper
        hoursMenu.items.add(hourHeight)
        hoursMenu.items.add(SeparatorMenuItem())
        val hours = intArrayOf(4, 6, 8, 10, 12, 18, 24)
        for (h in hours) {
            val labelText = String.format(strings.getString("calendar_menu_hours"), h)
            val wrapper = Label(labelText)
            val item = MenuItem()
            item.graphic = wrapper
            item.setOnAction { evt ->
                weekView.earlyLateHoursStrategy = DayViewBase.EarlyLateHoursStrategy.SHOW
                weekView.hoursLayoutStrategy = DayViewBase.HoursLayoutStrategy.FIXED_HOUR_COUNT
                weekView.visibleHours = h
            }
            hoursMenu.items.add(item)
        }
        return hoursMenu
    }
}