package lt.markmerkk.widgets

import com.google.common.eventbus.Subscribe
import lt.markmerkk.DisplayType
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventChangeDisplayType
import lt.markmerkk.widgets.calendar.CalendarWidget
import lt.markmerkk.widgets.list.ListLogWidget
import tornadofx.*

/**
 * Main container navigator
 * Lifecycle: [onAttach], [onDetach]
 */
class MainContainerNavigator(
    private val eventBus: WTEventBus,
    private val uiComponent: UIComponent,
    private val activeDisplayRepository: ActiveDisplayRepository
) {

    fun onAttach() {
        eventBus.register(this)
    }

    fun onDetach() {
        eventBus.unregister(this)
    }

    @Subscribe
    fun onDisplayTypeChange(eventChangeDisplayType: EventChangeDisplayType) {
        when (eventChangeDisplayType.displayType) {
            DisplayType.TABLE_VIEW_DETAIL -> {
                activeDisplayRepository.changeDisplayType(DisplayTypeLength.DAY)
                uiComponent.replaceWith<ListLogWidget>()
            }
            DisplayType.CALENDAR_VIEW_DAY -> {
                activeDisplayRepository.changeDisplayType(DisplayTypeLength.DAY)
                uiComponent.replaceWith<CalendarWidget>()
            }
            DisplayType.CALENDAR_VIEW_WEEK -> {
                activeDisplayRepository.changeDisplayType(DisplayTypeLength.WEEK)
                uiComponent.replaceWith<CalendarWidget>()
            }
            DisplayType.GRAPHS -> {}
        }
    }
}