package lt.markmerkk.clientextension

import com.fasterxml.jackson.databind.JsonNode
import net.rcarz.jiraclient.Field
import net.rcarz.jiraclient.IssueType
import net.rcarz.jiraclient.RestClient
import net.rcarz.jiraclient.Status
import org.slf4j.LoggerFactory

class IssueTypeExt(
    restClient: RestClient,
    jsonNode: JsonNode,
): IssueType(restClient, jsonNode) {

    var statuses: List<Status> = emptyList()
        private set

    init {
        statuses = defensiveStatusFetch(restClient, jsonNode)
    }

    fun defensiveStatusFetch(
        restClient: RestClient,
        jsonNode: JsonNode,
    ): List<Status> {
        return try {
            return Field.getResourceArray(Status::class.java, jsonNode.get("statuses"), restClient)
        } catch (ex: Exception) {
            l.warn("Error fetching status", ex)
            emptyList()
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(javaClass.simpleName)!!
    }
}