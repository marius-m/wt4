package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.Strings
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.mvp.*
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.utils.hourglass.HourGlass
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class ClockEditController : Initializable, ClockEditMVP.View {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonDismiss: JFXButton
    @FXML lateinit var jfxDateFrom: JFXDatePicker
    @FXML lateinit var jfxTimeFrom: JFXTimePicker
    @FXML lateinit var jfxDateTo: JFXDatePicker
    @FXML lateinit var jfxTimeTo: JFXTimePicker
    @FXML lateinit var jfxHint: Label
    @FXML lateinit var jfxSubtractFrom: JFXButton
    @FXML lateinit var jfxSubtractTo: JFXButton
    @FXML lateinit var jfxAppendFrom: JFXButton
    @FXML lateinit var jfxAppendTo: JFXButton
    @FXML lateinit var jfxTextFieldTicket: JFXTextField
    @FXML lateinit var jfxTextFieldComment: JFXTextArea
    @FXML lateinit var jfxButtonSave: JFXButton

    @Inject lateinit var hourglass: HourGlass
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var strings: Strings

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler

    private lateinit var clockEditPresenter: ClockEditMVP.Presenter
    private lateinit var timeQuickModifier: TimeQuickModifier
    private lateinit var logEditService: LogEditService

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)
        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        val timeQuickModifierListener: TimeQuickModifier.Listener = object : TimeQuickModifier.Listener {
            override fun onTimeModified(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
                clockEditPresenter.updateDateTime(
                        startDateTime.toLocalDate(),
                        startDateTime.toLocalTime(),
                        endDateTime.toLocalDate(),
                        endDateTime.toLocalTime()
                )
            }
        }
        timeQuickModifier = TimeQuickModifierImpl(
                timeQuickModifierListener
        )
        clockEditPresenter = ClockEditPresenterImpl(this, hourglass)
        logEditService = LogEditServiceImpl(
                LogEditInteractorImpl(storage),
                object : LogEditService.Listener {
                    override fun onDataChange(
                            startDateTime: LocalDateTime,
                            endDateTime: LocalDateTime,
                            ticket: String,
                            comment: String
                    ) {
                        uiBridgeDateTimeHandler.changeDate(
                                startDateTime,
                                endDateTime
                        )
                    }

                    override fun onDurationChange(durationAsString: String) {
                        jfxHint.text = durationAsString
                    }

                    override fun onGenericNotification(notification: String) {}

                    override fun onEntitySaveComplete() {
                        eventBus.post(
                                EventSnackBarMessage(
                                        strings.getString("clock_event_save_complete")
                                )
                        )
                        hourglass.restart()
                        jfxDialog.close()
                    }

                    override fun onEntitySaveFail(error: Throwable) {
                        logger.error("Error saving log.", error)
                        jfxHint.text = error.message ?: "Error saving. Check logs for more info."
                    }

                    override fun onEnableInput() {}

                    override fun onDisableInput() {}

                    override fun onEnableSaving() {}

                    override fun onDisableSaving() {}

                }
        )
        logEditService.serviceType = LogEditService.ServiceType.CREATE
        uiBridgeTimeQuickEdit = UIBridgeTimeQuickEdit(
                jfxSubtractFrom,
                jfxSubtractTo,
                jfxAppendFrom,
                jfxAppendTo,
                jfxDateFrom,
                jfxTimeFrom,
                jfxDateTo,
                jfxTimeTo,
                timeQuickModifier
        )
        uiBridgeDateTimeHandler = UIBridgeDateTimeHandler(
                jfxDateFrom = jfxDateFrom,
                jfxTimeFrom = jfxTimeFrom,
                jfxDateTo = jfxDateTo,
                jfxTimeTo = jfxTimeTo,
                timeQuickModifier = null,
                clockEditPresenter = clockEditPresenter,
                logEditService = logEditService
        )
        jfxButtonSave.setOnAction {
            logEditService.saveEntity(
                    startDate = jfxDateFrom.value,
                    startTime = jfxTimeFrom.value,
                    endDate = jfxDateTo.value,
                    endTime = jfxTimeTo.value,
                    task = jfxTextFieldTicket.text,
                    comment = jfxTextFieldComment.text
            )
        }
        clockEditPresenter.onAttach()
        uiBridgeDateTimeHandler.onAttach()
    }

    @PreDestroy
    fun destroy() {
        uiBridgeDateTimeHandler.onDetach()
        clockEditPresenter.onDetach()
    }

    //region MVP Impl

    override fun onDateChange(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
        uiBridgeDateTimeHandler.changeDate(startDateTime, endDateTime)
    }

    override fun onHintChange(hint: String) {
        jfxHint.text = hint
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(ClockEditController::class.java)!!
    }

}