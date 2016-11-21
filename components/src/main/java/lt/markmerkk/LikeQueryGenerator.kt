package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-11-21
 *
 * Generates query for more accurate results from database
 */
interface LikeQueryGenerator {
    fun genQueryFromInput(input: String): String
}