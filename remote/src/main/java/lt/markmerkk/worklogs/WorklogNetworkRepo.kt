package lt.markmerkk.worklogs

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.TicketStorage
import lt.markmerkk.UserSettings

class WorklogNetworkRepo(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraWorklogSearch: JiraWorklogSearch,
        private val ticketsDatabaseRepo: TicketStorage,
        private val userSettings: UserSettings
) {

}