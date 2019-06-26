package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleGroup
import lt.markmerkk.DisplayType
import lt.markmerkk.Main
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventChangeDisplayType
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class DisplaySelectDialogController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonCancel: JFXButton

    @FXML lateinit var jfxRadioTreeDetail: JFXRadioButton
    @FXML lateinit var jfxRadioCalendarDay: JFXRadioButton
    @FXML lateinit var jfxRadioCalendarWeek: JFXRadioButton
    @FXML lateinit var jfxRadioGraphs: JFXRadioButton
    @FXML lateinit var jfxToggleGroup: ToggleGroup

    @Inject lateinit var eventBus: WTEventBus

    var currentDisplayType: DisplayType = DisplayType.CALENDAR_VIEW_DAY
        set(value) {
            field = value
            changeStateFromDisplayType(value)
        }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)

        jfxButtonCancel.setOnAction {
            jfxDialog.close()
        }
    }

    @PreDestroy
    fun destroy() {
        jfxToggleGroup.selectedToggleProperty().removeListener(selectToggleChangeListener)
    }

    fun changeStateFromDisplayType(displayType: DisplayType) {
        when (displayType) {
            DisplayType.TABLE_VIEW_DETAIL -> jfxRadioTreeDetail.isSelected = true
            DisplayType.CALENDAR_VIEW_DAY -> jfxRadioCalendarDay.isSelected = true
            DisplayType.CALENDAR_VIEW_WEEK -> jfxRadioCalendarWeek.isSelected = true
            DisplayType.GRAPHS -> jfxRadioGraphs.isSelected = true
            else -> throw IllegalStateException("Display cannot be handled")
        }
        jfxToggleGroup.selectedToggleProperty().addListener(selectToggleChangeListener)
    }

    fun extractStateFromToggleGroup(): DisplayType {
        when (jfxToggleGroup.selectedToggle) {
            jfxRadioTreeDetail -> return DisplayType.TABLE_VIEW_DETAIL
            jfxRadioCalendarDay -> return DisplayType.CALENDAR_VIEW_DAY
            jfxRadioCalendarWeek -> return DisplayType.CALENDAR_VIEW_WEEK
            jfxRadioGraphs -> return DisplayType.GRAPHS
        }
        throw IllegalStateException("Nothing is selected")
    }

    private val selectToggleChangeListener: ChangeListener<Toggle> = object : ChangeListener<Toggle> {
        override fun changed(observable: ObservableValue<out Toggle>?, oldValue: Toggle?, newValue: Toggle?) {
            jfxDialog.close()
            eventBus.post(EventChangeDisplayType(extractStateFromToggleGroup()))
        }
    }


}