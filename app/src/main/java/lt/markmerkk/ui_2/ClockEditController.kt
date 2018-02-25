package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.mvp.ClockEditMVP
import lt.markmerkk.mvp.ClockEditPresenterImpl
import lt.markmerkk.mvp.TimeQuickModifier
import lt.markmerkk.mvp.TimeQuickModifierImpl
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.utils.hourglass.HourGlass
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

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler

    private lateinit var clockEditPresenter: ClockEditMVP.Presenter
    private lateinit var timeQuickModifier: TimeQuickModifier

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
                logEditService = null
        )
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

}