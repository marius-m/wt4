package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Region
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer

/**
 * @author mariusmerkevicius
 * @since 2016-10-26
 */
class GraphDrawerXYBars(
        override val title: String
) : GraphDrawer<SimpleLog> {

    private var data: List<SimpleLog> = emptyList()

    override fun populateData(data: List<SimpleLog>) {
        this.data = data
    }

    override fun createGraph(): Region {
        val yAxis = NumberAxis()
        yAxis.tickLabelFormatter = TimeSpentAxisFormatter()
        val xAxis = CategoryAxis()
        val bc = BarChart(xAxis, yAxis)
        bc.title = title
        val displayData = mutableListOf<XYChart.Data<String, Number>>()
        val dataMap = assembleIssues(data)
        dataMap.forEach { displayData.add(XYChart.Data<String, Number>(it.key, it.value)) }
        val seriesList = FXCollections.observableArrayList<XYChart.Series<String, Number>>().apply {
            add(XYChart.Series(
                    "Issues",
                    FXCollections.observableArrayList(displayData)
            ))
        }
        return BarChart(xAxis, yAxis, seriesList)
    }

    //region Convenience

    fun assembleIssues(logs: List<SimpleLog>): Map<String, Number> {
        val mappedLogs = mutableMapOf<String, Number>()
        logs.forEach {
            if (mappedLogs.containsKey(it.task)) {
                mappedLogs.put(it.task, it.duration + mappedLogs.get(it.task) as Long)
            } else {
                mappedLogs.put(it.task, it.duration)
            }
        }
        return mappedLogs
    }

    //endregion

    override fun toString(): String = title

}