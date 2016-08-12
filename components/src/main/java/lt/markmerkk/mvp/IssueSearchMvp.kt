package lt.markmerkk.mvp

import lt.markmerkk.entities.LocalIssue
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
interface IssueSearchMvp {
    interface View {
        fun showIssues(result: List<LocalIssue>)
        fun hideIssues()
    }
    interface Presenter {
        fun onAttach()
        fun onDetach()

        fun search(issuePhrase: String)
    }
}