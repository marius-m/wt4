package lt.markmerkk.widgets

import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.skins.JFXDatePickerContentLocal
import javafx.scene.Parent
import javafx.scene.layout.VBox
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.TimeProvider
import tornadofx.*
import javax.inject.Inject

class DatePickerWidget: View() {

    @Inject lateinit var logStorage: LogStorage

    lateinit var viewContainer: VBox

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        viewContainer = vbox {
            add(createDatePickerContent())
        }
    }

    override fun onDock() {
        super.onDock()
        viewContainer.children.clear()
        viewContainer.children.add(createDatePickerContent())
    }

    // Generate date picker as there is no way to change current target date after view inflation
    private fun createDatePickerContent(): JFXDatePickerContentLocal {
        val viewDatePicker = JFXDatePicker(TimeProvider.toJavaLocalDate(logStorage.targetDate))
        viewDatePicker.defaultColor = Styles.cActiveRed
        viewDatePicker.valueProperty().addListener(ChangeListener { _, _, newValue ->
            logStorage.targetDate = TimeProvider.toJodaDate(newValue).toDateTimeAtStartOfDay()
            close()
        })
        return JFXDatePickerContentLocal(viewDatePicker)
    }

}