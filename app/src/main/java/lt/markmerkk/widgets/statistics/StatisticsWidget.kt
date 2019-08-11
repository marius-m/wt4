package lt.markmerkk.widgets.statistics

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogUtils
import tornadofx.*
import javax.inject.Inject

class StatisticsWidget: View(), StatisticsContract.View {

    @Inject lateinit var logStorage: LogStorage

    init {
        Main.component().inject(this)
    }

    private lateinit var viewLabelTotal: Label
    private lateinit var viewPiechart: PieChart

    private lateinit var presenter: StatisticsContract.Presenter
    private val pieChartData = mutableListOf<PieChart.Data>()
            .observable()

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            label("Statistics") {
                addClass(Styles.dialogHeader)
            }
        }
        center {
            vbox {
                viewPiechart = piechart("Tickets worked on", pieChartData) {
                    vgrow = Priority.ALWAYS
                }
                viewLabelTotal = label("Total: ") {
                    addClass(Styles.labelRegular)
                    vgrow = Priority.NEVER
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Dismiss".toUpperCase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter = StatisticsPresenter(logStorage)
        viewLabelTotal.text = "Total: ${presenter.totalAsString()}"
        val logsAsPieChartData = presenter.mapData()
                .map { PieChart.Data(it.key, it.value.toDouble()) }
        pieChartData.clear()
        pieChartData.addAll(logsAsPieChartData)
    }

    override fun onUndock() {
        pieChartData.clear()
        super.onUndock()
    }

}