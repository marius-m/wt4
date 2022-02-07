package lt.markmerkk.ui_2.views.date

import javafx.stage.StageStyle
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.TimeProvider
import lt.markmerkk.datepick.DateSelectRequest
import lt.markmerkk.datepick.DateSelectType
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.utils.DateSwitcherFormatter
import lt.markmerkk.widgets.datepicker.DatePickerWidget
import org.joda.time.LocalDate
import tornadofx.find

class QuickDateChangeWidgetPresenterDefault(
    private val resultDispatcher: ResultDispatcher,
    private val logStorage: LogStorage
) : DateChangeContract.Presenter {

    private var view: DateChangeContract.View? = null

    override fun onAttach(view: DateChangeContract.View) {
        this.view = view
        logStorage.register(dataListener)
        view.render(activeDateAsString())
    }

    override fun onDetach() {
        this.view = null
        logStorage.unregister(dataListener)
    }

    override fun selectDate(localDate: LocalDate) {
        logStorage.targetDate = localDate.toDateTimeAtStartOfDay()
    }

    override fun onClickNext() {
        when (logStorage.displayType) {
            DisplayTypeLength.DAY -> logStorage.targetDate = logStorage.targetDate.plusDays(1)
            DisplayTypeLength.WEEK -> logStorage.targetDate = logStorage.targetDate.plusDays(7)
        }
    }

    override fun onClickPrev() {
        when (logStorage.displayType) {
            DisplayTypeLength.DAY -> logStorage.targetDate = logStorage.targetDate.minusDays(1)
            DisplayTypeLength.WEEK -> logStorage.targetDate = logStorage.targetDate.minusDays(7)
        }
    }

    override fun onClickDate() {
        resultDispatcher.publish(
            key = DatePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
            resultEntity = DateSelectRequest(
                dateSelection = logStorage.targetDate.toLocalDate(),
                extra = DateSelectType.TARGET_DATE.name
            )
        )
        find<DatePickerWidget>().openModal(
                stageStyle = StageStyle.DECORATED,
                block = true,
                resizable = false
        )
    }

    private fun activeDateAsString(): String {
        val localDate = TimeProvider.toJavaLocalDate(logStorage.targetDate)
        return when (logStorage.displayType) {
            DisplayTypeLength.DAY -> DateSwitcherFormatter.formatDateForDay(localDate)
            DisplayTypeLength.WEEK -> DateSwitcherFormatter.formatDateForWeek(localDate)
        }
    }

    private val dataListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            view?.render(activeDateAsString())
        }
    }
}