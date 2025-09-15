package lt.markmerkk.widgets.datepicker

import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.skins.JFXDatePickerContentLocal
import javafx.beans.value.ChangeListener
import javafx.scene.Parent
import javafx.scene.layout.VBox
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Styles
import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.datepick.DateSelectRequest
import lt.markmerkk.datepick.DateSelectResult
import lt.markmerkk.events.EventChangeDate
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class DatePickerWidget: BaseFragment() {

    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var eventBus: WTEventBus

    private lateinit var viewDatePicker: JFXDatePicker
    private lateinit var viewContainer: VBox
    private lateinit var request: DateSelectRequest

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        viewContainer = vbox {
            style {
                minWidth = 300.px
                maxWidth = 300.px
                minHeight = 340.px
                maxHeight = 340.px
            }
        }
    }

    override fun onDock() {
        super.onDock()
        this.request = resultDispatcher
            .consume(RESULT_DISPATCH_KEY_PRESELECT, DateSelectRequest::class.java) ?: DateSelectRequest.asDefault()
        viewContainer.children.clear()
        val datePickerContent = createDatePickerContent(this.request.dateSelection)
        this.viewDatePicker = datePickerContent.jfxDatePicker
        viewContainer.children.add(datePickerContent)
        viewDatePicker.valueProperty().addListener(listenerDateSelect)
    }

    override fun onUndock() {
        viewDatePicker.valueProperty().removeListener(listenerDateSelect)
        super.onUndock()
    }

    // Generate date picker as there is no way to change current target date after view inflation
    private fun createDatePickerContent(
        preselectDate: LocalDate
    ): JFXDatePickerContentLocal {
        val viewDatePicker = JFXDatePicker(TimeProvider.toJavaLocalDate(preselectDate))
        viewDatePicker.defaultColor = Styles.cActiveRed
        return JFXDatePickerContentLocal(viewDatePicker)
    }

    //region Listeners

    private val listenerDateSelect = ChangeListener<java.time.LocalDate> { _, _, newValue ->
        val jodaNewDate = TimeProvider.toJodaDate(newValue)
        resultDispatcher.publish(
            key = RESULT_DISPATCH_KEY_RESULT,
            resultEntity = DateSelectResult
                .withNewValue(request, jodaNewDate)
        )
        eventBus.post(EventChangeDate)
        close()
    }

    //endregion

    companion object {
        private val l = LoggerFactory.getLogger(DatePickerWidget::class.java)!!
        const val RESULT_DISPATCH_KEY_PRESELECT = "4215c5e7-6967-447e-912b-081f46ab05ff"
        const val RESULT_DISPATCH_KEY_RESULT = "eee216f1-98e6-47a2-b0dd-f3556d16e3d1"
    }
}