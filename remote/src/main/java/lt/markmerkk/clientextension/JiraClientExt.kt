package lt.markmerkk.clientextension

import com.fasterxml.jackson.databind.JsonNode
import net.rcarz.jiraclient.ICredentials
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.Resource
import net.rcarz.jiraclient.User

/**
 * [net.rcarz.jiraclient.JiraClient] with extended functionality
 */
class JiraClientExt constructor(
    private val host: String,
    private val creds: ICredentials?,
): JiraClient(host as String?, creds) {

    /**
     * Obtains information current user from session
     * Works in same way as [.currentUser], but uses a workaround for latest API
     * https://community.atlassian.com/t5/Jira-questions/API-breaking-change-bug-quot-The-accountId-query-parameter-needs/qaq-p/1350776
     *
     * @return user
     * @throws net.rcarz.jiraclient.JiraException failed to obtain the project
     */
    @Throws(JiraException::class)
    fun currentUser(): User {
        try {
            val uriAuth = restclient.buildURI(Resource.getBaseUri() + "myself")
            val responseAuth: JsonNode = restclient.get(uriAuth)
            return User(restclient, responseAuth)
        } catch (ex: Exception) {
            throw JiraException(ex.message, ex)
        }
    }
}