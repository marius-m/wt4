package lt.markmerkk

/**
 * Generic jira filter
 */
interface JiraFilter<in T> {
    fun valid(input: T?): Boolean
}