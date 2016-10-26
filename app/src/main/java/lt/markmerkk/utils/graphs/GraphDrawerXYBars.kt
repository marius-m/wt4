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
        val years = listOf("WT", "CAR")
        val xAxis = CategoryAxis()
        val yAxis = NumberAxis()
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, null, "ms")
        val bc = BarChart<String, Number>(xAxis, yAxis)
        bc.maxWidth = Double.MAX_VALUE
        bc.maxHeight = Double.MAX_VALUE

        // setup chart
        bc.title = title
        xAxis.label = "Issues"
        xAxis.categories = FXCollections.observableList(years)
        yAxis.label = "Invested hours"

        // add starting data
        val series1 = XYChart.Series<String, Number>()
        series1.name = "Data Series 1"

        // create sample data
        series1.data.add(XYChart.Data(years[0], 567))
        series1.data.add(XYChart.Data(years[1], 1292))
        series1.data.add(XYChart.Data(years[2], 2180))
        bc.data.add(series1)
        return bc
    }

}