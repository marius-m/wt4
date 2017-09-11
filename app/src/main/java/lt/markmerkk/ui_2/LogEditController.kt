package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.util.StringConverter
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.mvp.LogEditInteractorImpl
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
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
    @FXML lateinit var jfxTextFieldTicketLink: Hyperlink
    @FXML lateinit var jfxTextFieldComment: JFXTextArea
    @FXML lateinit var jfxTextFieldHint: Label
    @FXML lateinit var jfxTextFieldHint2: Label

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var hostServices: HostServicesInteractor

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")!!
    private val dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")!!
    private lateinit var logEditService: LogEditService

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
        jfxButtonAccept.setOnAction {
            logEditService.saveEntity(
                    startDate = jfxDateFrom.value,
                    startTime = jfxTimeFrom.value,
                    endDate = jfxDateTo.value,
                    endTime = jfxTimeTo.value,
                    task = jfxTextFieldTicket.text,
                    comment = jfxTextFieldComment.text
            )
        }
        jfxTextFieldTicketLink.setOnAction {
//            hostServices.openExternalIssue(jfxTextFieldTicket.text)
            hostServices.copyLinkToClipboard(jfxTextFieldTicket.text)
        }
        jfxTextFieldTicketLink.tooltip = Tooltip("Copy issue link to clipboard")
        jfxTextFieldTicketLink.graphic = linkGraphic(Color.BLACK, 16.0, 20.0)
    }

    /**
     * Called directly from the view, whenever entity is ready for control.
     * If entity is provided, will provide update for the entity, otherwise it will create new one.
     */
    fun initFromView(entity: SimpleLog?) {
        logEditService = LogEditServiceImpl(
                LogEditInteractorImpl(logStorage),
                this
        )
        if (entity != null) {
            logEditService.serviceType = LogEditService.ServiceType.UPDATE
            logEditService.entityInEdit = entity
            jfxHeaderLabel.text = "Update log"
            jfxButtonAccept.text = "UPDATE"
        } else {
            logEditService.serviceType = LogEditService.ServiceType.CREATE
            jfxHeaderLabel.text = "Create new log"
            jfxButtonAccept.text = "CREATE"
        }
        logEditService.redraw()
        jfxDateFrom.valueProperty().addListener(startDateChangeListener)
        jfxTimeFrom.valueProperty().addListener(startTimeChangeListener)
        jfxDateTo.valueProperty().addListener(endDateChangeListener)
        jfxTimeTo.valueProperty().addListener(endTimeChangeListener)
    }

    @PreDestroy
    fun destroy() {
        jfxTimeTo.valueProperty().removeListener(endTimeChangeListener)
        jfxDateTo.valueProperty().removeListener(endDateChangeListener)
        jfxTimeFrom.valueProperty().removeListener(startTimeChangeListener)
        jfxDateFrom.valueProperty().removeListener(startDateChangeListener)
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
        jfxTextFieldHint.text = durationAsString
    }

    override fun onGenericNotification(notification: String) {
        jfxTextFieldHint2.text = notification
    }

    override fun onEntitySaveComplete() {
        jfxDialog.close()
    }

    override fun onEntitySaveFail(error: Throwable) {
        jfxTextFieldHint.text = error.message ?: "Error saving entity!"
    }

    override fun onEnableInput() {
        jfxTextFieldTicket.isEditable = true
        jfxTextFieldComment.isEditable = true
        jfxDateFrom.isEditable = true
        jfxTimeFrom.isEditable = true
        jfxDateTo.isEditable = true
        jfxTimeTo.isEditable = true
    }

    override fun onDisableInput() {
        jfxTextFieldTicket.isEditable = false
        jfxTextFieldComment.isEditable = false
        jfxDateFrom.isEditable = false
        jfxTimeFrom.isEditable = false
        jfxDateTo.isEditable = false
        jfxTimeTo.isEditable = false
    }

    override fun onEnableSaving() {
        jfxButtonAccept.isDisable = false
    }

    override fun onDisableSaving() {
        jfxButtonAccept.isDisable = true
    }

    //endregion

    //region Graphs

    private fun linkGraphic(color: Color, width: Double, height: Double): SVGGlyph {
        val svgGlyph = SVGGlyph(
                -1,
                "link",
                "M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z",
                color
        )
        svgGlyph.setSize(width, height)
        return svgGlyph
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

    private val startDateChangeListener = ChangeListener<LocalDate> { observable, oldValue, newValue ->
        logEditService.updateDateTime(
                startDate = newValue,
                startTime = jfxTimeFrom.value,
                endDate = jfxDateTo.value,
                endTime = jfxTimeTo.value
        )
    }

    private val startTimeChangeListener = ChangeListener<LocalTime> { observable, oldValue, newValue ->
        logEditService.updateDateTime(
                startDate = jfxDateFrom.value,
                startTime = newValue,
                endDate = jfxDateTo.value,
                endTime = jfxTimeTo.value
        )
    }

    private val endDateChangeListener = ChangeListener<LocalDate> { observable, oldValue, newValue ->
        logEditService.updateDateTime(
                startDate = jfxDateFrom.value,
                startTime = jfxTimeFrom.value,
                endDate = newValue,
                endTime = jfxTimeTo.value
        )
    }

    private val endTimeChangeListener = ChangeListener<LocalTime> { observable, oldValue, newValue ->
        logEditService.updateDateTime(
                startDate = jfxDateFrom.value,
                startTime = jfxTimeFrom.value,
                endDate = jfxDateTo.value,
                endTime = newValue
        )
    }

    //endregion

}