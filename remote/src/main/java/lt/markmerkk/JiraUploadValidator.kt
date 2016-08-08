package lt.markmerkk

import org.apache.commons.logging.impl.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
class JiraUploadValidator() : JiraFilter<SimpleLog> {
    override fun valid(input: SimpleLog?): Boolean {
        return false
    }
}