package lt.markmerkk

import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import rx.Observable
import rx.Subscriber

/**
 * Created by mariusmerkevicius on 1/29/16.
 * Responsible for validating credengials and connecting
 * [JiraClient]
 */
class JiraConnector(
        val hostname: String,
        val username: String,
        val password: String
) : Observable.OnSubscribe<JiraClient> {
    override fun call(subscriber: Subscriber<in JiraClient>) {
        try {
            val jira = JiraClient(hostname, BasicCredentials(username, password))

            subscriber.onNext(jira)
            subscriber.onCompleted()
        } catch (e: JiraException) {
            subscriber.onError(e)
        }
    }
}
