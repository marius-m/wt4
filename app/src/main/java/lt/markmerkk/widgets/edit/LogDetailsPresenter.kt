package lt.markmerkk.widgets.edit

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import lt.markmerkk.*
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import javax.inject.Inject

class LogDetailsPresenter(
        private val logStorage: LogStorage,
        private val hostServices: HostServicesInteractor,
        private val eventBus: EventBus,
        private val graphics: Graphics<SVGGlyph>,
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val resultDispatcher: ResultDispatcher,
        private val schedulerProvider: SchedulerProvider,
        private val timeProvider: TimeProvider
): LogDetailsContract.Presenter {

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
    private lateinit var logEditService: LogEditService
    private lateinit var ticketInfoLoader: TicketInfoLoader

    private var view: LogDetailsContract.View? = null

    override fun onAttach(view: LogDetailsContract.View) {
        this.view = view

    }

    override fun onDetach() {
        this.view = null
    }
}