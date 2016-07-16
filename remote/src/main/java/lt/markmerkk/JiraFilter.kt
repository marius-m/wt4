package lt.markmerkk

/**
 * Generic jira filter
 */
interface JiraFilter<T> {
    fun valid(input: T?): Boolean
}