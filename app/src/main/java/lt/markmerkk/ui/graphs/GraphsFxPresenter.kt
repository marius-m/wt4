package lt.markmerkk.ui.graphs

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.HBox
import lt.markmerkk.Main
import lt.markmerkk.mvp.GraphMvp
import lt.markmerkk.mvp.GraphPresenterImpl
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
class GraphsFxPresenter : Initializable, GraphMvp.View {

    @FXML
    lateinit var viewGraphType: ComboBox<String>
    @FXML
    lateinit var viewGraphContainer: HBox
    @FXML
    lateinit var viewDatePickerFrom: DatePicker
    @FXML
    lateinit var viewDatePickerTo: DatePicker

    val viewProgress = ProgressIndicator()

    val presenter by lazy {
        GraphPresenterImpl(this)
    }

    init {
        viewProgress.setMaxSize(150.0, 150.0)
        viewProgress.isVisible = false
        viewProgress.isManaged = false
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        viewGraphContainer.children.addAll(viewProgress)

        presenter.onAttach()
    }

    @PreDestroy
    fun destroy() {
        presenter.onDetach()
    }

    override fun showProgress() {
        viewProgress.isVisible = true
        viewProgress.isManaged = true
    }

    override fun hideProgress() {
        viewProgress.isVisible = false
        viewProgress.isManaged = false
    }

    override fun showGraph() {
    }

    override fun showErrorGraph(message: String) {
    }

    fun createChart(): BarChart<String, Number> {
        val years = listOf("2007", "2008", "2009")
        val xAxis = CategoryAxis()
        val yAxis = NumberAxis()
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, "$", null)
        val bc = BarChart<String, Number>(xAxis, yAxis)
        bc.maxWidth = Double.MAX_VALUE
        bc.maxHeight = Double.MAX_VALUE
        // setup chart
        bc.title = "Advanced Bar Chart"
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
        // create sample data
        series1.getData().add(XYChart.Data(years[0], 567))
        series1.getData().add(XYChart.Data(years[1], 1292))
        series1.getData().add(XYChart.Data(years[2], 2180))
        series2.getData().add(XYChart.Data(years[0], 956))
        series2.getData().add(XYChart.Data(years[1], 1665))
        series2.getData().add(XYChart.Data(years[2], 2450))
        series3.getData().add(XYChart.Data(years[0], 800))
        series3.getData().add(XYChart.Data(years[1], 1000))
        series3.getData().add(XYChart.Data(years[2], 2800))
        bc.data.add(series1)
        bc.data.add(series2)
        bc.data.add(series3)
        return bc
    }

}