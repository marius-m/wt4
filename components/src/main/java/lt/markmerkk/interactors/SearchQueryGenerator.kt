package lt.markmerkk.interactors

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 *
 * Will modify input by
 * 1. Form task issue
 * 2. Split description string
 */
interface SearchQueryGenerator {
    /**
     * Generates a serach query
     */
    fun searchQuery(input: String): String
}