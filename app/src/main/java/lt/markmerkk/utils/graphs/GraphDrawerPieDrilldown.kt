package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.scene.chart.PieChart
import javafx.scene.chart.XYChart
import javafx.scene.layout.Region
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
class GraphDrawerPieDrilldown(
        override val title: String
) : GraphDrawer<SimpleLog> {

    private var data: List<SimpleLog> = emptyList()

    override fun populateData(data: List<SimpleLog>) {
        this.data = data
    }

    override fun createGraph(): Region {
        val displayData = mutableListOf<PieChart.Data>()
//        val dataMap = assembleIssues(data)
//        dataMap.forEach { displayData.add(PieChart.Data(it.key, it.value.toDouble())) }
        return PieChart(FXCollections.observableList(displayData))
    }

    override fun toString(): String {
        return title
    }

}