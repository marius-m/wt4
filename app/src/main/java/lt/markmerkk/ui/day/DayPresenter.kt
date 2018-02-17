package lt.markmerkk.ui.day

import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.calendarfx.view.DetailedDayView
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.StackPane
import lt.markmerkk.GAStatics
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui_2.LogStatusView
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class DayPresenter : Initializable {
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var tracker: ITracker

    @FXML private lateinit var jfxDayContainer: StackPane
    @FXML private lateinit var jfxDayView: DetailedDayView
    private lateinit var jfxInfoDialog: LogStatusView

    private lateinit var dayLoader: DayViewLoader

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        jfxInfoDialog = LogStatusView(jfxDayContainer)
        tracker.sendView(GAStatics.VIEW_CALENDAR_DAY)
        storage.register(storageListener)

        val calendar = com.calendarfx.model.Calendar()
        calendar.setStyle(com.calendarfx.model.Calendar.Style.STYLE1)
        calendar.isReadOnly = true
        val calendarSource = CalendarSource()
        calendarSource.calendars.addAll(calendar)
        jfxDayView.calendarSources.add(calendarSource)
        jfxDayView.isShowAllDayView = false

        dayLoader = DayViewLoader(
                object : DayViewLoader.View {
                    override fun onCalendarEntries(calendarEntries: List<Entry<String>>) {
                        calendar.startBatchUpdates()
                        calendar.clear()
                        calendar.addEntries(calendarEntries)
                        calendar.stopBatchUpdates()
                    }
                },
                Schedulers.io(),
                JavaFxScheduler.getInstance()
        )
        dayLoader.onAttach()
        dayLoader.load(storage.data)
    }

    @PreDestroy
    fun destroy() {
        dayLoader.onDetach()
        storage.unregister(storageListener)
    }

    //region Listeners

    private val storageListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            dayLoader.load(data)
        }
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(DayPresenter::class.java)!!
    }

}
