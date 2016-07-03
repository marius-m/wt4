package lt.markmerkk

import javafx.util.Pair
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
class JiraObservables2 {
    fun remoteWorklogs(): Observable<Pair<Issue, List<WorkLog>>> {
        return Observable.empty()
    }
}