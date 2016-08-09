package lt.markmerkk.mvp

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.LocalIssue
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
class IssueSyncPresenterImpl(
        private val view: IssueSyncMvp.View,
        private val jiraClientProvider: JiraClientProvider,
        private val jiraInteractor: JiraInteractor,
        private val dataStorage: IDataStorage<LocalIssue>
) : IssueSyncMvp.Presenter {

    val subscription: Subscription? = null

    override fun onAttach() {
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun sync() {
    }

}