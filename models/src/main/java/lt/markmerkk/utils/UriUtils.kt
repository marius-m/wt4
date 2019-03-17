package lt.markmerkk.utils

import lt.markmerkk.Const
import lt.markmerkk.Tags
import lt.markmerkk.entities.isEmpty
import org.apache.http.Consts
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URISyntaxException

object UriUtils {

    private val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!

    /**
     * Takes in worklog uri and parses out remote ID of it
     * @return ID or [Consts.NO_ID]
     */
    fun parseUri(url: String?): Long {
        if (url.isEmpty()) return Const.NO_ID
        try {
            val uri = URI(url)
            val segments = uri.path.split("/")
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val idString = segments[segments.size - 1]
            return java.lang.Long.parseLong(idString)
        } catch (e: URISyntaxException) {
            logger.warn("Error parsing ID of remote", e)
            return Const.NO_ID
        } catch (e: NumberFormatException) {
            logger.warn("Error parsing ID of remote", e)
            return Const.NO_ID
        }
    }
}