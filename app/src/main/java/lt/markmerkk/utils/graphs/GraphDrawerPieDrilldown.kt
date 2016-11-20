package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.chart.PieChart
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDataProviderPieChart
import lt.markmerkk.interactors.GraphDrawer
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.utils.LogUtils

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
class GraphDrawerPieDrilldown(
        override val title: String,
        private val graphPieProviderPieChart: GraphDataProviderPieChart,
        private val hostServicesInteractor: HostServicesInteractor
) : GraphDrawer<SimpleLog> {

    private var data: List<SimpleLog> = emptyList()

    override val isRefreshable: Boolean = true
    private val pieChart = PieChart()

    override fun populateData(data: List<SimpleLog>) {
        this.data = data
    }

    override fun createGraph(): Region {
        refresh()
        return pieChart
    }

    override fun refresh() {
        pieChart.data = parentData(data)
        pieChart.legendSide = Side.LEFT
        pieChart.data.forEach {
            it.node.onMouseClicked = object : EventHandler<MouseEvent> {
                override fun handle(event: MouseEvent?) {
                    pieChart.data = childData(data, it.name)
                    pieChart.data.forEach {
                        addTooltip(data, it)
                        it.node.onMouseClicked = object : EventHandler<MouseEvent> {
                            override fun handle(event: MouseEvent?) {
                                hostServicesInteractor.openExternalIssue(it.name)
                            }
                        }
                    }
                }
            }
            addTooltip(data, it)
        }
    }

    //region Convenience

    fun parentData(data: List<SimpleLog>): ObservableList<PieChart.Data> {
        val dataMap = graphPieProviderPieChart.assembleParentData(data)
        val displayData = mutableListOf<PieChart.Data>()
        dataMap.forEach { displayData.add(PieChart.Data(it.key, it.value)) }
        return FXCollections.observableList(displayData)
    }

    fun childData(data: List<SimpleLog>, filter: String): ObservableList<PieChart.Data>  {
        val dataMap = graphPieProviderPieChart.assembleChildData(data, filter)
        val displayData = mutableListOf<PieChart.Data>()
        dataMap.forEach { displayData.add(PieChart.Data(it.key, it.value)) }
        return FXCollections.observableList(displayData)
    }

    fun addTooltip(data: List<SimpleLog>, chartData: PieChart.Data) {
        val percentUsed = graphPieProviderPieChart.percentInData(chartData.name, data)
        val timeUsed = LogUtils.formatDuration(graphPieProviderPieChart.timeSpentInData(chartData.name, data).toLong())
        Tooltip.install(
                chartData.node,
                Tooltip("${chartData.name} / ${String.format("%.1f", percentUsed)}% / $timeUsed")
        )
    }

    //endregion


    override fun toString(): String {
        return title
    }

}