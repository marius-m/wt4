package lt.markmerkk

import lt.markmerkk.mvp.UserSettings
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
class JiraClientProviderImpl(
        private val userSettings: UserSettings
) : JiraClientProvider {

    var jiraClient: JiraClient? = null

    val clientObservable: Observable<JiraClient> // Cache client
        get() {
            if (jiraClient == null) {
                return Observable.create(
                        JiraConnector(
                                hostname = userSettings.host,
                                username = userSettings.username,
                                password = userSettings.password
                        )
                )
            }
            return Observable.just(jiraClient)
        }

    override fun clientObservable(): Observable<JiraClient> {
        return clientObservable.flatMap {
            jiraClient = it
            Observable.just(it)
        }
    }

    override fun reset() {
        jiraClient = null
    }

}