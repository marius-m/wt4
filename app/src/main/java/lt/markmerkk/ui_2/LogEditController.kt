package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.skins.JFXTimePickerContent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.util.StringConverter
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
import org.joda.time.format.DateTimeFormat
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class LogEditController : Initializable, LogEditService.Listener {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonAccept: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton
    @FXML lateinit var jfxContentView: BorderPane
    @FXML lateinit var jfxHeaderLabel: Label

    @FXML lateinit var jfxDateFrom: JFXDatePicker
    @FXML lateinit var jfxTimeFrom: JFXTimePicker
    @FXML lateinit var jfxDateTo: JFXDatePicker
    @FXML lateinit var jfxTimeTo: JFXTimePicker
    @FXML lateinit var jfxTextFieldTicket: JFXTextField
    @FXML lateinit var jfxTextFieldComment: JFXTextArea

    @Inject lateinit var logStorage: LogStorage

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")!!
    val dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")!!
    lateinit var logEditService: LogEditService
    lateinit var entity: SimpleLog

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)
        jfxButtonCancel.setOnAction {
            jfxDialog.close()
        }
        jfxDateFrom.converter = dateConverter
        jfxTimeFrom.converter = timeConverter
        jfxTimeFrom.setIs24HourView(true)
        jfxDateTo.converter = dateConverter
        jfxTimeTo.converter = timeConverter
        jfxTimeTo.setIs24HourView(true)
    }

    /**
     * Called directly from the view, whenever entity is ready for control
     */
    fun initFromView(entity: SimpleLog) {
        this.entity = entity
        logEditService = LogEditServiceImpl(this, entity)
        logEditService.onAttach()
    }

    @PreDestroy
    fun destroy() {
        logEditService.onDetach()
    }

    //region Edit service callbacks

    override fun onDataChange(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            ticket: String,
            comment: String
    ) {
        jfxDateFrom.value = startDateTime.toLocalDate()
        jfxTimeFrom.value = startDateTime.toLocalTime()

        jfxDateTo.value = endDateTime.toLocalDate()
        jfxTimeTo.value = endDateTime.toLocalTime()
        jfxTextFieldTicket.text = ticket
        jfxTextFieldComment.text = comment
    }

    override fun onDurationChange(durationAsString: String) {
    }

    override fun onEntitySaveComplete() {
    }

    override fun onEnableInput() {
    }

    override fun onDisableInput() {
    }

    //endregion

    //region Listeners

    private val dateConverter: StringConverter<LocalDate> = object : StringConverter<LocalDate>() {
        override fun toString(date: LocalDate): String {
            return date.format(dateFormatter)
        }

        override fun fromString(string: String): LocalDate {
            return LocalDate.parse(string)
        }

    }

    private val timeConverter: StringConverter<LocalTime> = object : StringConverter<LocalTime>() {
        override fun toString(date: LocalTime): String {
            return date.format(timeFormatter)
        }

        override fun fromString(string: String): LocalTime {
            return LocalTime.parse(string)
        }

    }

    //endregion

}