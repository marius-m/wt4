package lt.markmerkk.widgets.statistics

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class StatisticsWidget: Fragment(), StatisticsContract.View {

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var ticketStorage: TicketStorage
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var hourGlass: HourGlass

    init {
        Main.component().inject(this)
    }

    private lateinit var viewLabelTicketInfo: Label
    private lateinit var viewLabelTicketDuration: Label
    private lateinit var viewLabelTotal: Label
    private lateinit var viewPiechart: PieChart

    private lateinit var presenter: StatisticsContract.Presenter
    private lateinit var ticketInfoLoader: TicketInfoLoader
    private val pieChartData = mutableListOf<PieChart.Data>()
            .observable()

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            label("Statistics") {
                addClass(Styles.dialogHeader)
            }
        }
        center {
            vbox(spacing = 4) {
                viewPiechart = piechart("Tickets worked on", pieChartData) {
                    vgrow = Priority.ALWAYS
                }
                viewLabelTicketInfo = label {
                    addClass(Styles.labelRegular)
                }
                viewLabelTicketDuration = label {
                    addClass(Styles.labelRegular)
                }
                viewLabelTotal = label("Total: ") {
                    addClass(Styles.labelRegular)
                    vgrow = Priority.NEVER
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Close".toUpperCase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter = StatisticsPresenter(logStorage, hourGlass)
        ticketInfoLoader = TicketInfoLoader(
                listener = ticketInfoLoaderListener,
                ticketStorage = ticketStorage,
                waitScheduler = schedulerProvider.waitScheduler(),
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        viewLabelTicketInfo.text = "Click on pie chart to find more info about work"
        viewLabelTicketDuration.text = "Ticket duration: No ticket selected"
        viewLabelTotal.text = "Total: ${presenter.totalAsString()}"

        presenter.onAttach(this)
        ticketInfoLoader.onAttach()

        val logsAsPieChartData: List<PieChart.Data> = presenter.mapData()
                .map { PieChart.Data(it.key, it.value.toDouble()) }
        pieChartData.clear()
        pieChartData.addAll(logsAsPieChartData)
        logsAsPieChartData
                .forEach { data ->
                    data.node.addEventHandler(MouseEvent.MOUSE_CLICKED) {
                        ticketInfoLoader.findTicket(data.name)
                        viewLabelTicketDuration.text = "Ticket duration: ${LogUtils.formatShortDurationMillis(data.pieValue.toLong())}"
                    }
                }
    }

    override fun onUndock() {
        ticketInfoLoader.onDetach()
        presenter.onDetach()
        pieChartData.clear()
        super.onUndock()
    }

    //region Listeners

    private val ticketInfoLoaderListener = object : TicketInfoLoader.Listener {
        override fun onTicketFound(ticket: Ticket) {
            viewLabelTicketInfo.text = "Ticket info: ${ticket.code.code} - ${ticket.description}"
        }

        override fun onNoTicket(searchTicket: String) {
            viewLabelTicketInfo.text = "Ticket info: No info on '$searchTicket'"
        }

    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(StatisticsWidget::class.java)!!
    }

}