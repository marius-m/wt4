package lt.markmerkk.ui.graphs

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.HBox
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.GraphDrawer
import lt.markmerkk.mvp.GraphMvp
import lt.markmerkk.mvp.GraphPresenterImpl
import lt.markmerkk.mvp.LogInteractorImpl
import lt.markmerkk.mvp.QueryResultProviderImpl
import lt.markmerkk.utils.graphs.GraphDrawerXYBars
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
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
    @FXML
    lateinit var viewGraphType: ComboBox<GraphDrawer<*>>
    @FXML
    lateinit var viewGraphContainer: HBox
    @FXML
    lateinit var viewDatePickerFrom: DatePicker
    @FXML
    lateinit var viewDatePickerTo: DatePicker
    @FXML
    lateinit var viewProgress: ProgressIndicator

    val graphs: List<GraphDrawer<*>> = listOf(
            GraphDrawerXYBars("Simple")
    )
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
            presenter.loadGraph()
        }

        presenter.onAttach()
        viewGraphType.selectionModel.select(0)
        presenter.loadGraph()
    }

    @PreDestroy
    fun destroy() {
        presenter.onDetach()
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
        viewGraphContainer.children.clear()
        viewGraphContainer.children.add(drawer.createGraph())
        viewGraphContainer.isVisible = true
    }

    override fun showErrorGraph(message: String) {
        println("Error: $message")
    }

    //endregion

}