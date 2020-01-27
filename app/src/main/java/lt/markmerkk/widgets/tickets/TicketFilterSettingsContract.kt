package lt.markmerkk.widgets.tickets

import lt.markmerkk.tickets.TicketStatusesLoader

interface TicketFilterSettingsContract {

    interface View: TicketStatusesLoader.Listener {
        fun cleanUpAndExit()
    }

    interface Presenter {
        fun onAttach()
        fun onDetach()
        fun loadTicketStatuses()
        fun saveTicketStatuses(
                ticketStatusViewModels: List<TicketStatusViewModel>,
                useOnlyCurrentUser: Boolean,
                filterIncludeAssignee: Boolean,
                filterIncludeReporter: Boolean,
                filterIncludeIsWatching: Boolean
        )
    }

}