package lt.markmerkk

import lt.markmerkk.entities.SimpleLog
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
class JiraFilterSimpleLog() : JiraFilter<SimpleLog> {
    override fun valid(input: SimpleLog?): Boolean {
        if (input == null) throw JiraFilter.FilterErrorException("Object is invalid!")
        if (input.task.isNullOrEmpty()) throw JiraFilter.FilterErrorException("Task id is empty!")
        if (input.comment.isNullOrEmpty()) throw JiraFilter.FilterErrorException("Comment is empty!")
        if (input.isError) throw JiraFilter.FilterErrorException("Already has an error!")
        if (!input.isDirty) {
            logger.info("Already in sync: $input")
            return false
        }
        return true
    }

    companion object {
        val logger = LoggerFactory.getLogger(JiraFilterSimpleLog::class.java)!!
    }

}