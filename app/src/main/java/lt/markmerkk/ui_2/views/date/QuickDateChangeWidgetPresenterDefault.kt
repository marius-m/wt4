package lt.markmerkk.ui_2.views.date

import javafx.stage.StageStyle
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.datepick.DateSelectRequest
import lt.markmerkk.datepick.DateSelectType
import lt.markmerkk.utils.DateSwitcherFormatterJoda
import lt.markmerkk.widgets.datepicker.DatePickerWidget
import org.joda.time.LocalDate
import tornadofx.find

class QuickDateChangeWidgetPresenterDefault(
    private val resultDispatcher: ResultDispatcher,
    private val activeDisplayRepository: ActiveDisplayRepository
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
        activeDisplayRepository.changeDisplayDate(localDate)
    }

    override fun onClickNext() {
        activeDisplayRepository.nextDisplayDate()
    }

    override fun onClickPrev() {
        activeDisplayRepository.prevDisplayDate()
    }

    override fun onClickDate() {
        resultDispatcher.publish(
            key = DatePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
            resultEntity = DateSelectRequest(
                dateSelection = activeDisplayRepository.displayDateRange.selectDate,
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
        val localDate = activeDisplayRepository.displayDateRange.start
        return when (activeDisplayRepository.displayType) {
            DisplayTypeLength.DAY -> DateSwitcherFormatterJoda.formatDateForDay(localDate)
            DisplayTypeLength.WEEK -> DateSwitcherFormatterJoda.formatDateForWeek(localDate)
        }
    }
}