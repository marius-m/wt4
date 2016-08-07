package lt.markmerkk

import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraClientProvider {

    fun reset()

    /**
     * Will return a client and cache it
     */
    fun clientObservable(): Observable<JiraClient>

}