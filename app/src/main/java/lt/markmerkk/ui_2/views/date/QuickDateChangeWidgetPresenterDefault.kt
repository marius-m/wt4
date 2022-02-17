package lt.markmerkk.ui_2.views.date

import javafx.stage.StageStyle
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.LogRepository
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.datepick.DateSelectRequest
import lt.markmerkk.datepick.DateSelectType
import lt.markmerkk.utils.DateSwitcherFormatterJoda
import lt.markmerkk.widgets.datepicker.DatePickerWidget
import org.joda.time.LocalDate
import tornadofx.find

class QuickDateChangeWidgetPresenterDefault(
    private val resultDispatcher: ResultDispatcher,
    private val logRepository: LogRepository
) : DateChangeContract.Presenter {

    private var view: DateChangeContract.View? = null

    override fun onAttach(view: DateChangeContract.View) {
        this.view = view
        view.render(activeDateAsString())
    }

    override fun onDetach() {
        this.view = null
    }

    override fun selectDate(localDate: LocalDate) {
        logRepository.changeActiveDate(localDate.toDateTimeAtStartOfDay())
    }

    override fun onClickNext() {
        when (logRepository.displayType) {
            DisplayTypeLength.DAY -> {
                logRepository.changeActiveDate(logRepository.targetDate.plusDays(1))
            }
            DisplayTypeLength.WEEK -> {
                logRepository.changeActiveDate(logRepository.targetDate.plusDays(7))
            }
        }
    }

    override fun onClickPrev() {
        when (logRepository.displayType) {
            DisplayTypeLength.DAY -> {
                logRepository.changeActiveDate(logRepository.targetDate.minusDays(1))
            }
            DisplayTypeLength.WEEK -> {
                logRepository.changeActiveDate(logRepository.targetDate.minusDays(7))
            }
        }
    }

    override fun onClickDate() {
        resultDispatcher.publish(
            key = DatePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
            resultEntity = DateSelectRequest(
                dateSelection = logRepository.targetDate.toLocalDate(),
                extra = DateSelectType.TARGET_DATE.name
            )
        )
        find<DatePickerWidget>().openModal(
                stageStyle = StageStyle.DECORATED,
                block = true,
                resizable = false
        )
    }

    override fun activeDateAsString(): String {
        val localDate = logRepository.targetDate.toLocalDate()
        return when (logRepository.displayType) {
            DisplayTypeLength.DAY -> DateSwitcherFormatterJoda.formatDateForDay(localDate)
            DisplayTypeLength.WEEK -> DateSwitcherFormatterJoda.formatDateForWeek(localDate)
        }
    }
}