package lt.markmerkk.ui.day

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.util.Callback
import jfxtras.scene.control.agenda.Agenda
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.LogStatusService
import lt.markmerkk.mvp.LogStatusServiceImpl
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.ui.week.AgendaPresenter
import lt.markmerkk.ui.week.AgendaPresenterImpl2
import lt.markmerkk.ui.week.AgendaView
import lt.markmerkk.ui.week.AppointmentSimpleLog
import lt.markmerkk.ui_2.LogStatusView
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

    @FXML private lateinit var jfxDayContainer: StackPane
    @FXML private lateinit var jfxAgenda: Agenda
    private lateinit var jfxInfoDialog: LogStatusView

    private lateinit var skin: CustomAgendaDayView
    private lateinit var agendaPresenter: AgendaPresenter
    private lateinit var updateListener: UpdateListener

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        jfxInfoDialog = LogStatusView(jfxDayContainer)
        tracker.sendView(GAStatics.VIEW_WEEK)
        skin = CustomAgendaDayView(jfxAgenda)
        skin.refreshWithDate(storage.targetDate)
        jfxAgenda.locale = java.util.Locale("en")
        jfxAgenda.allowDragging = false
        jfxAgenda.allowResize = false
        jfxAgenda.skin = skin
        agendaPresenter = AgendaPresenterImpl2(
                this,
                jfxAgenda.appointmentGroups()[0],
                jfxAgenda.appointmentGroups()[10],
                jfxAgenda.appointmentGroups()[13],
                Schedulers.computation(),
                JavaFxScheduler.getInstance()
        )
        agendaPresenter.onAttach()
        agendaPresenter.reloadView(storage.data)
        storage.register(storageListener)
        jfxAgenda.editAppointmentCallbackProperty().set(agendaCallbackListener)
        StackPane.setAlignment(jfxInfoDialog.view, Pos.TOP_RIGHT)
        jfxAgenda.setOnMouseClicked {
            if (jfxAgenda.selectedAppointments().size > 0) {
                val firstAppointmentAsSimpleLogId = (jfxAgenda.selectedAppointments().first() as AppointmentSimpleLog)
                        .simpleLog
                        ._id
                jfxInfoDialog.showLogWithId(firstAppointmentAsSimpleLogId)
            } else {
                jfxInfoDialog.showLogWithId(null)
            }
        }
    }

    fun setUpdateListener(updateListener: UpdateListener) {
        this.updateListener = updateListener
    }

    @PreDestroy
    fun destroy() {
        jfxAgenda.editAppointmentCallbackProperty().set(null)
        storage.unregister(storageListener)
        agendaPresenter.onDetatch()
    }

    //region Listeners

    private val storageListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            agendaPresenter.reloadView(storage.data)
            skin.refreshWithDate(storage.targetDate)
        }
    }

    override fun updateAgenda(appointments: List<Agenda.AppointmentImplLocal>) {
        jfxAgenda.appointments().clear()
        jfxAgenda.appointments().addAll(appointments)
        jfxAgenda.skin = skin
    }

    private var agendaCallbackListener: Callback<Agenda.Appointment, Void> = object : Callback<Agenda.Appointment, Void> {
        override fun call(appointment: Agenda.Appointment): Void? {
            val contextMenu = ContextMenu()
            val updateItem = MenuItem(Translation.getInstance().getString("general_update"),
                    ImageView(Image(javaClass.getResource("/update_2.png").toString())))
            updateItem.onAction = EventHandler<ActionEvent> {
                updateListener.onUpdate((appointment as AppointmentSimpleLog).simpleLog)
            }
            val deleteItem = MenuItem(Translation.getInstance().getString("general_delete"),
                    ImageView(Image(javaClass.getResource("/delete_2.png").toString())))
            deleteItem.onAction = EventHandler<ActionEvent> {
                updateListener.onDelete((appointment as AppointmentSimpleLog).simpleLog)
            }
            val cloneItem = MenuItem(Translation.getInstance().getString("general_clone"),
                    ImageView(Image(javaClass.getResource("/clone_2.png").toString())))
            cloneItem.onAction = EventHandler<ActionEvent> {
                updateListener.onClone((appointment as AppointmentSimpleLog).simpleLog)
            }
            contextMenu.items.addAll(updateItem, deleteItem, cloneItem)
            jfxAgenda.contextMenu = contextMenu
            return null
        }
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(DayPresenter::class.java)!!
    }

}
