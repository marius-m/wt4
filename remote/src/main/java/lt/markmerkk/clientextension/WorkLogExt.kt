package lt.markmerkk.clientextension

import com.fasterxml.jackson.databind.JsonNode
import net.rcarz.jiraclient.RestClient
import net.rcarz.jiraclient.WorkLog

/**
 * Extended functionality for [net.rcarz.jiraclient.WorkLog]
 */
class WorkLogExt(
    restClient: RestClient,
    jsonNode: JsonNode,
): WorkLog(restClient, jsonNode)