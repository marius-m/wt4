package lt.markmerkk.clientextension

import com.fasterxml.jackson.databind.JsonNode
import net.rcarz.jiraclient.ICredentials
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.Resource
import net.rcarz.jiraclient.Resource.getBaseUri
import net.rcarz.jiraclient.User
import org.slf4j.LoggerFactory

/**
 * [net.rcarz.jiraclient.JiraClient] with extended functionality
 */
class JiraClientExt constructor(
    private val host: String,
    private val creds: ICredentials?,
) : JiraClient(host as String?, creds) {

    /**
     * Obtains information current user from session
     * Works in same way as [.currentUser], but uses a workaround for latest API
     * https://community.atlassian.com/t5/Jira-questions/API-breaking-change-bug-quot-The-accountId-query-parameter-needs/qaq-p/1350776
     *
     * @return user
     * @throws net.rcarz.jiraclient.JiraException failed to obtain the project
     */
    @Throws(JiraException::class)
    fun fetchCurrentUser(): User {
        try {
            val uriAuth = restclient.buildURI(Resource.getBaseUri() + "myself")
            val responseAuth: JsonNode = restclient.get(uriAuth)
            return User(restclient, responseAuth)
        } catch (ex: Exception) {
            throw JiraException(ex.message, ex)
        }
    }

    /**
     * Obtains all possible statuses, given its project key.
     * @param key the project key
     * @return the project
     * @throws JiraException failed to obtain the project
     */
    @Throws(JiraException::class)
    fun fetchProjectStatuses(key: String): Set<String> {
        try {
            l.debug("Pulling project statuses(key: ${key})")
            val uri = restclient.buildURI("${getBaseUri()}project/${key}/statuses")
            val responseIssueStatusNodes = restclient.get(uri)

            l.debug("Resolving issue types(responseIssueTypes: ${responseIssueStatusNodes})")
            val issueTypes = responseIssueStatusNodes.map { issueStatusNode ->
                IssueTypeExt(restclient, issueStatusNode)
            }

            val projectStatuses: Set<String> = issueTypes
                .flatMap { issueType -> issueType.statuses }
                .map { status -> status.name }
                .toSet()
            l.debug("Flattening issue statuses(projectStatuses: ${projectStatuses})")
            return projectStatuses
        } catch (ex: Exception) {
            throw JiraException("Error pulling / flattening issue statuses", ex);
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(javaClass.simpleName)!!
    }
}