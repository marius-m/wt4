package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ToggleGroup
import lt.markmerkk.DisplayType
import lt.markmerkk.Main
import lt.markmerkk.events.EventChangeDisplayType
import java.net.URL
import java.util.*
import javax.inject.Inject

class DisplaySelectDialogController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonAccept: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton

    @FXML lateinit var jfxRadioTreeSimple: JFXRadioButton
    @FXML lateinit var jfxRadioTreeDetail: JFXRadioButton
    @FXML lateinit var jfxRadioCalendarDay: JFXRadioButton
    @FXML lateinit var jfxRadioCalendarWeek: JFXRadioButton
    @FXML lateinit var jfxToggleGroup: ToggleGroup

    @Inject lateinit var eventBus: EventBus

    var currentDisplayType: DisplayType = DisplayType.TABLE_VIEW_SIMPLE
        set(value) {
            field = value
            changeStateFromDisplayType(value)
        }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)

        jfxButtonCancel.setOnAction {
            jfxDialog.close()
        }
        jfxButtonAccept.setOnAction {
            jfxDialog.close()
            eventBus.post(EventChangeDisplayType(extractStateFromToggleGroup()))
        }
    }

    fun changeStateFromDisplayType(displayType: DisplayType) {
        when (displayType) {
            DisplayType.TABLE_VIEW_SIMPLE -> jfxRadioTreeSimple.isSelected = true
            DisplayType.TABLE_VIEW_DETAIL -> jfxRadioTreeDetail.isSelected = true
            DisplayType.CALENDAR_VIEW_DAY -> jfxRadioCalendarDay.isSelected = true
            DisplayType.CALENDAR_VIEW_WEEK -> jfxRadioCalendarWeek.isSelected = true
            else -> throw IllegalStateException("Display cannot be handled")
        }
    }

    fun extractStateFromToggleGroup(): DisplayType {
        when (jfxToggleGroup.selectedToggle) {
            jfxRadioTreeSimple -> return DisplayType.TABLE_VIEW_SIMPLE
            jfxRadioTreeDetail -> return DisplayType.TABLE_VIEW_DETAIL
            jfxRadioCalendarDay -> return DisplayType.CALENDAR_VIEW_DAY
            jfxRadioCalendarWeek -> return DisplayType.CALENDAR_VIEW_WEEK
        }
        throw IllegalStateException("Nothing is selected")
    }

}