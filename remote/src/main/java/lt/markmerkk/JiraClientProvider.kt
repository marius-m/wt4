package lt.markmerkk

import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraClientProvider {

    @Throws(IllegalStateException::class)
    fun client(): JiraClient

    fun reset()

}