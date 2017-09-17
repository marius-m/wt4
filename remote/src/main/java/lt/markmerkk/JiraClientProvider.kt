package lt.markmerkk

import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraClientProvider {

    val username: String

    fun client(
            hostname: String,
            username: String,
            password: String
    ): JiraClient

    /**
     * Creates a new client or takes it from cache with default user saved settings
     */
    @Throws(IllegalStateException::class)
    fun client(): JiraClient

}