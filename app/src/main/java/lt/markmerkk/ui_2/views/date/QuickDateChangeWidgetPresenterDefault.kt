package lt.markmerkk.ui_2.views.date

import javafx.scene.layout.StackPane
import javafx.stage.StageStyle
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.utils.DateSwitcherFormatter
import lt.markmerkk.widgets.DatePickerWidget
import tornadofx.*

class QuickDateChangeWidgetPresenterDefault(
        private val externalSourceNode: ExternalSourceNode<StackPane>,
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
        find<DatePickerWidget>().openModal(
                stageStyle = StageStyle.UTILITY,
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