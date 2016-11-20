package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.chart.PieChart
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDataProviderPieChart
import lt.markmerkk.interactors.GraphDrawer

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
class GraphDrawerPieDrilldown(
        override val title: String,
        val graphPieProviderPieChart: GraphDataProviderPieChart
) : GraphDrawer<SimpleLog> {

    private var data: List<SimpleLog> = emptyList()

    override fun populateData(data: List<SimpleLog>) {
        this.data = data
    }

    override fun createGraph(): Region {
        val displayData = mutableListOf<PieChart.Data>()
        val dataMap = graphPieProviderPieChart.assembleParentData(data)
        dataMap.forEach { displayData.add(PieChart.Data(it.key, it.value)) }
        val pieChart = PieChart(FXCollections.observableArrayList(displayData))
        pieChart.legendSide = Side.LEFT
        pieChart.data.forEach {
            addTooltipWithDetails(it)
            it.node.onMouseClicked = object : EventHandler<MouseEvent> {
                override fun handle(event: MouseEvent?) {
                    pieChart.data = FXCollections.observableArrayList(
                            PieChart.Data("WT-4", 50000000.0),
                            PieChart.Data("WT-5", 50000000.0)
                    )
                }
            }
        }
        return pieChart
    }

    // todo : Incomplete impl (real data missing)
    fun addTooltipWithDetails(chartData: PieChart.Data) {
        val percentUsed = "50%"
        val timeSpent = "1h 32m 52s"
        Tooltip.install(
                chartData.node,
                Tooltip("${chartData.name} / $percentUsed / $timeSpent")
        )
    }

    override fun toString(): String {
        return title
    }

}