package lt.markmerkk

import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraClientProvider {

    val username: String

    @Throws(IllegalStateException::class)
    fun client(): JiraClient

    fun reset()

}