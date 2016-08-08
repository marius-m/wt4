package lt.markmerkk

/**
 * Generic jira filter
 */
interface JiraFilter<in T> {
    /**
     * Will output a true if validation succeeds
     * or throws an error with an error message indicating a problem
     */
    @Throws(FilterErrorException::class)
    fun valid(input: T?): Boolean

    /**
     * Thrown whenever there is a problem filtering
     */
    class FilterErrorException(message: String) : Exception(message)
}