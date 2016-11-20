package lt.markmerkk.ui.graphs

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.util.StringConverter
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.GraphDataProviderPieChartImpl
import lt.markmerkk.interactors.GraphDrawer
import lt.markmerkk.mvp.*
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.graphs.GraphDrawerPieDrilldown
import lt.markmerkk.utils.graphs.GraphDrawerXYBars
import org.joda.time.DateTime
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.awt.BorderLayout
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
class GraphsFxPresenter : Initializable, GraphMvp.View {

    @Inject
    lateinit var executor: IExecutor
    @Inject
    lateinit var hostServicesInteractor: HostServicesInteractor
    @FXML
    lateinit var viewGraphType: ComboBox<GraphDrawer<*>>
    @FXML
    lateinit var viewGraphContainer: BorderPane
    @FXML
    lateinit var viewDatePickerFrom: DatePicker
    @FXML
    lateinit var viewDatePickerTo: DatePicker
    @FXML
    lateinit var viewProgress: ProgressIndicator
    @FXML
    lateinit var buttonRefresh: Button

    val graphs: List<GraphDrawer<*>> by lazy {
        listOf(
                GraphDrawerXYBars(
                        "Simple graph of worked issues",
                        hostServicesInteractor
                ), //todo : Add translation
                GraphDrawerPieDrilldown(
                        "Drilldown on worked issues", //todo: Translation
                        GraphDataProviderPieChartImpl(),
                        hostServicesInteractor
                )
        )
    }

    val presenter by lazy {
        GraphPresenterImpl(
                view = this,
                logInteractor = LogInteractorImpl(
                        QueryResultProviderImpl<List<SimpleLog>>(executor),
                        Schedulers.io()
                ),
                graphDrawers = graphs,
                uiScheduler = JavaFxScheduler.getInstance(),
                ioScheduler = Schedulers.io()
        )
    }

    //region Life-cycle

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        viewProgress.isVisible = false
        viewProgress.isManaged = false
        viewGraphType.items = FXCollections.observableList(graphs)
        viewGraphType.setOnAction {
            presenter.selectGraphIndex = viewGraphType.selectionModel.selectedIndex
            loadGraph()
        }
        viewDatePickerFrom.converter = dateConverter
        viewDatePickerFrom.editor.text = LogFormatters.shortFormatDate.print(DateTime())
        viewDatePickerFrom.setOnAction { loadGraph() }
        viewDatePickerTo.converter = dateConverter
        viewDatePickerTo.editor.text = LogFormatters.shortFormatDate.print(DateTime())
        viewDatePickerTo.setOnAction { loadGraph() }

        presenter.onAttach()
        viewGraphType.selectionModel.select(0)
        loadGraph()
    }

    @PreDestroy
    fun destroy() {
        presenter.onDetach()
    }

    /**
     * Event from SCENE BUILDER
     */
    fun onClickRefresh() {
        presenter.refresh()
    }

    //endregion

    //region MVP

    override fun showProgress() {
        viewProgress.isVisible = true
        viewProgress.isManaged = true
    }

    override fun hideProgress() {
        viewProgress.isVisible = false
        viewProgress.isManaged = false
    }

    override fun showGraph(drawer: GraphDrawer<*>) {
        viewGraphContainer.center = drawer.createGraph()
        viewGraphContainer.isVisible = true
    }

    override fun showErrorGraph(message: String) {
        println("Error: $message")
    }

    override fun showRefreshButton() {
        buttonRefresh.isVisible = true
        buttonRefresh.isManaged = true
    }

    override fun hideRefreshButton() {
        buttonRefresh.isVisible = false
        buttonRefresh.isManaged = false
    }

    //endregion

    //region Graph loading

    fun loadGraph() {
        val fromDateTime = LogFormatters.shortFormatDate.parseDateTime(viewDatePickerFrom.editor.text.toString())
                .withTime(0, 0, 0, 0)
        val toDateTime = LogFormatters.shortFormatDate.parseDateTime(viewDatePickerTo.editor.text.toString())
                .withTime(0, 0, 0, 0)
                .plusDays(1)
        presenter.loadGraph(fromDateTime.millis, toDateTime.millis)
    }

    val dateConverter: StringConverter<LocalDate> = object : StringConverter<LocalDate>() {
        override fun toString(date: LocalDate?): String {
            if (date == null) return LogFormatters.longFormat.print(DateTime.now())
            val updateTime = DateTime().withDate(
                    date.year,
                    date.monthValue,
                    date.dayOfMonth
            )
            return LogFormatters.shortFormatDate.print(updateTime)
        }

        override fun fromString(string: String): LocalDate {
            try {
                val dateTime = LogFormatters.shortFormatDate.parseDateTime(string)
                return LocalDate.of(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth)
            } catch (e: IllegalArgumentException) {
                val oldTime = DateTime.now()
                return LocalDate.of(oldTime.year, oldTime.monthOfYear, oldTime.dayOfMonth)
            }

        }
    }

    //endregion

}