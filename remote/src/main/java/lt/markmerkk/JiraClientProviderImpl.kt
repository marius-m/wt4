package lt.markmerkk

import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
class JiraClientProviderImpl(
        val host: String?,
        val username: String?,
        val password: String?
) : JiraClientProvider {

    var jiraClient: JiraClient? = null

    val clientObservable: Observable<JiraClient> // Cache client
        get() {
            if (jiraClient == null) {
                return Observable.create(JiraConnector(host, username, password))
            }
            return Observable.just(jiraClient)
        }

    override fun clientObservable(): Observable<JiraClient> {
        return clientObservable.flatMap {
            jiraClient = it
            Observable.just(it)
        }
    }

}