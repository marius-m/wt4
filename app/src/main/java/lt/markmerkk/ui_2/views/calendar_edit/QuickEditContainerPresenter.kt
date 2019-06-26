package lt.markmerkk.ui_2.views.calendar_edit

import com.google.common.eventbus.Subscribe
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventLogSelection

class QuickEditContainerPresenter(
        private val eventBus: WTEventBus
): QuickEditContract.ContainerPresenter {

    private var view: QuickEditContract.ContainerView? = null

    override fun onAttach(view: QuickEditContract.ContainerView) {
        this.view = view
        eventBus.register(this)
    }

    override fun onDetach() {
        this.view = null
        eventBus.unregister(this)
    }

    @Subscribe
    fun onSelectionChange(event: EventLogSelection) {
        view?.changeLogSelection(event.selectedLogId)
    }

}