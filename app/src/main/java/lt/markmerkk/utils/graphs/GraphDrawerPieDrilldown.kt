package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.scene.chart.PieChart
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
        return PieChart(FXCollections.observableList(displayData))
    }

    override fun toString(): String {
        return title
    }

}