package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.skins.JFXDatePickerContent
import com.jfoenix.skins.JFXDatePickerSkin
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.StringConverter
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.utils.DateCompat
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class CurrentDayDialogController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    lateinit var jfxDatePicker: JFXDatePicker

    @Inject lateinit var logStorage: LogStorage

    private val maxHeight = 420.0
    private val maxWidth = 370.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)

        jfxDatePicker = JFXDatePicker(DateCompat.toJavaLocalDate(logStorage.targetDate))
        jfxDatePicker.defaultColor = Color.web("#E91E63")
        jfxDatePicker.valueProperty().addListener(dateChangeListener)
        val jfxCalendarPickerSkin = JFXDatePickerSkin(jfxDatePicker)
        val jfxPopupContent: JFXDatePickerContent = jfxCalendarPickerSkin.popupContent as JFXDatePickerContent

        jfxDialogLayout.children.add(StackPane(jfxPopupContent))
        jfxDialogLayout.padding = Insets(0.0, 0.0, 0.0, 0.0)
        jfxDialogLayout.setMinSize(maxWidth, maxHeight) // Default size for the date picker dialog
        jfxDialogLayout.setPrefSize(maxWidth, maxHeight) // Default size for the date picker dialog
        jfxDialogLayout.setMaxSize(maxWidth, maxHeight) // Default size for the date picker dialog
    }

    @PreDestroy
    fun destroy() {
        jfxDatePicker.valueProperty().removeListener(dateChangeListener)
    }

    // region Listeners

    val dateChangeListener: ChangeListener<LocalDate> = object : ChangeListener<LocalDate> {
        override fun changed(observable: ObservableValue<out LocalDate>, oldValue: LocalDate, newValue: LocalDate) {
            logStorage.targetDate = DateCompat.toJodaDateTime(newValue)
            jfxDialog.close()
        }
    }

    //endregion

}