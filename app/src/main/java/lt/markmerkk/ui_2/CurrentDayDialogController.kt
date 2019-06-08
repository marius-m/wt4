package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.skins.JFXDatePickerContentLocal
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.TimeProvider
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class CurrentDayDialogController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    private lateinit var jfxDatePicker: JFXDatePicker

    @Inject lateinit var logStorage: LogStorage

    private val maxHeight = 420.0
    private val maxWidth = 370.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        jfxDatePicker = JFXDatePicker(TimeProvider.toJavaLocalDate(logStorage.targetDate))
        jfxDatePicker.defaultColor = Color.web("#E91E63")
        jfxDatePicker.valueProperty().addListener(dateChangeListener)
        val jfxDatePickerContent = JFXDatePickerContentLocal(jfxDatePicker)

        jfxDialogLayout.setMinSize(maxWidth, maxHeight) // Default size for the date picker dialog
        jfxDialogLayout.setPrefSize(maxWidth, maxHeight) // Default size for the date picker dialog
        jfxDialogLayout.setMaxSize(maxWidth, maxHeight) // Default size for the date picker dialog
        jfxDatePickerContent.setMinSize(maxWidth, maxHeight)
        jfxDatePickerContent.setPrefSize(maxWidth, maxHeight)
        jfxDatePickerContent.setMaxSize(maxWidth, maxHeight)

        jfxDialogLayout.padding = Insets(0.0, 0.0, 0.0, 0.0)
        jfxDialogLayout.children.clear()
        jfxDialogLayout.children.add(StackPane(jfxDatePickerContent))
    }

    @PreDestroy
    fun destroy() {
        jfxDatePicker.valueProperty().removeListener(dateChangeListener)
    }

    // region Listeners

    private val dateChangeListener: ChangeListener<LocalDate> = ChangeListener { _, _, newValue ->
        logStorage.targetDate = TimeProvider.toJodaDate(newValue).toDateTimeAtStartOfDay()
        jfxDialog.close()
    }

    //endregion

}