package lt.markmerkk.utils.graphs

import javafx.collections.FXCollections
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Region
import lt.markmerkk.Translation
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDataProviderXY
import lt.markmerkk.interactors.GraphDrawer
import lt.markmerkk.mvp.HostServicesInteractor
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-10-26
 */
class GraphDrawerXYBars(
        override val title: String,
        private val dataProviderXY: GraphDataProviderXY,
        private val hostServicesInteractor: HostServicesInteractor
) : GraphDrawer<SimpleLog> {

    private var data: List<SimpleLog> = emptyList()
    override val isRefreshable: Boolean = false

    val barChart = BarChart<String, Number>(
            CategoryAxis(),
            NumberAxis()
                    .apply { tickLabelFormatter = TimeSpentAxisFormatter() }
    )

    override fun populateData(data: List<SimpleLog>) {
        this.data = data
    }

    override fun createGraph(): Region {
        val displayData = dataProviderXY.assembleIssues(data)
                .map { XYChart.Data<String, Number>(it.key, it.value) }
        val seriesList = FXCollections.observableArrayList<XYChart.Series<String, Number>>().apply {
            val seriesElement = XYChart.Series(
                    Translation.getInstance().getString("graph_simple_axis_x_title"),
                    FXCollections.observableArrayList(displayData)
            )
            add(seriesElement)
        }
        barChart.data = seriesList
        return barChart
    }

    override fun refresh() {
        throw UnsupportedOperationException("graph is not refreshable")
    }

    override fun toString(): String = title

    companion object {
        val logger = LoggerFactory.getLogger(GraphDrawerXYBars::class.java)!!
    }

}