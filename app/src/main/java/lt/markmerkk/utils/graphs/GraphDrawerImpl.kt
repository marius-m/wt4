package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Region
import lt.markmerkk.interactors.GraphDrawer

/**
 * @author mariusmerkevicius
 * @since 2016-10-26
 */
class GraphDrawerImpl(override val title: String) : GraphDrawer {

    override fun createGraph(): Region {
        val years = listOf("2007", "2008", "2009")
        val xAxis = CategoryAxis()
        val yAxis = NumberAxis()
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, "$", null)
        val bc = BarChart<String, Number>(xAxis, yAxis)
        bc.maxWidth = Double.MAX_VALUE
        bc.maxHeight = Double.MAX_VALUE
        // setup chart
        bc.title = title
        xAxis.label = "Year"
        xAxis.categories = FXCollections.observableList(years)
        yAxis.label = "Price"
        // add starting data
        val series1 = XYChart.Series<String, Number>()
        series1.name = "Data Series 1"
        val series2 = XYChart.Series<String, Number>()
        series2.name = "Data Series 2"
        val series3 = XYChart.Series<String, Number>()
        series3.name = "Data Series 3"
        val series4 = XYChart.Series<String, Number>()
        series4.name = "Data Series 4"
        // create sample data
        series1.data.add(XYChart.Data(years[0], 567))
        series1.data.add(XYChart.Data(years[1], 1292))
        series1.data.add(XYChart.Data(years[2], 2180))
        series2.data.add(XYChart.Data(years[0], 956))
        series2.data.add(XYChart.Data(years[1], 1665))
        series2.data.add(XYChart.Data(years[2], 2450))
        series3.data.add(XYChart.Data(years[0], 800))
        series3.data.add(XYChart.Data(years[1], 1000))
        series3.data.add(XYChart.Data(years[2], 2800))
        series4.data.add(XYChart.Data(years[0], 800))
        series4.data.add(XYChart.Data(years[1], 1000))
        series4.data.add(XYChart.Data(years[2], 2800))
        bc.data.add(series1)
        bc.data.add(series2)
        bc.data.add(series3)
        bc.data.add(series4)
        return bc
    }

}