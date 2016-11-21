package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-11-21
 *
 * Generates query for more accurate results from database
 */
interface LikeQueryGenerator {
    /**
     * Generates 'like %somehing%' clauses from input
     */
    fun genClauses(input: String): List<String>

    /**
     * Generates full query from clauses with OR seperator
     */
    fun genQuery(clauses: List<String>): String
}