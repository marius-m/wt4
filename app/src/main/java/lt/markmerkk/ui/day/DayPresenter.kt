package lt.markmerkk.ui.day

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.VBox
import jfxtras.scene.control.agenda.Agenda
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.ui.week.AgendaPresenter
import lt.markmerkk.ui.week.AgendaPresenterImpl2
import lt.markmerkk.ui.week.AgendaView
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class DayPresenter : Initializable, AgendaView {
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var tracker: ITracker

    @FXML private lateinit var agenda: Agenda

    private lateinit var skin: CustomAgendaDayView
    private lateinit var agendaPresenter: AgendaPresenter

    private var updateListener: UpdateListener? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

//        tracker.sendView(GAStatics.VIEW_WEEK) // todo : incorrect event
        val targetDate = storage.targetDate
        skin = CustomAgendaDayView(agenda, targetDate)
        agenda.locale = java.util.Locale("en")
        agenda.allowDragging = false
        agenda.allowResize = false
        agenda.skin = skin
        agendaPresenter = AgendaPresenterImpl2(
                this,
                agenda.appointmentGroups()[0],
                agenda.appointmentGroups()[10],
                agenda.appointmentGroups()[13],
                Schedulers.computation(),
                JavaFxScheduler.getInstance()
        )
        agendaPresenter.onAttach()
        agendaPresenter.reloadView(storage.data)
        storage.register(storageListener)
    }

    fun setUpdateListener(updateListener: UpdateListener) {
        this.updateListener = updateListener
    }

    @PreDestroy
    fun destroy() {
        storage.unregister(storageListener)
        agendaPresenter.onDetatch()
    }

    //region Listeners

    private val storageListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            agendaPresenter.reloadView(storage.data)
        }
    }

    override fun updateAgenda(appointments: List<Agenda.AppointmentImplLocal>) {
        agenda.appointments().clear()
        agenda.appointments().addAll(appointments)
        agenda.skin = skin
    }

    companion object {
        val logger = LoggerFactory.getLogger(DayPresenter::class.java)!!
    }

    //endregion

}
