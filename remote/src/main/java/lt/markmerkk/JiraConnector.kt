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
        val hostname: String?,
        val username: String?,
        val password: String?
) : Observable.OnSubscribe<JiraClient> {
    override fun call(subscriber: Subscriber<in JiraClient>) {
        try {
            if (hostname == null) throw IllegalStateException("Hostname cannot be empty")
            if (hostname.length == 0) throw IllegalStateException("Hostname cannot be empty")
            if (username == null) throw IllegalStateException("Username cannot be empty")
            if (username.length == 0) throw IllegalStateException("Username cannot be empty")
            if (password == null) throw IllegalStateException("Password cannot be empty")
            if (password.length == 0) throw IllegalStateException("Password cannot be empty")
            val jira = JiraClient(hostname, BasicCredentials(username, password))

            subscriber.onNext(jira)
            subscriber.onCompleted()
        } catch (e: IllegalStateException) {
            subscriber.onError(e)
        } catch (e: JiraException) {
            subscriber.onError(e)
        }
    }
}
