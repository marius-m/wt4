package lt.markmerkk

import java.util.*

/**
 * @author mariusmerkevicius
 * @since 2016-11-21
 */
class LikeQueryGeneratorImpl(
        private val key: String
) : LikeQueryGenerator {

    init {
        if (key.isEmpty()) throw IllegalArgumentException("invalid key")
    }

    override fun genQueryFromInput(input: String): String {
        throw UnsupportedOperationException()
    }

    //region Convenience

    /**
     * Generates valid 'like' clause
     */
    fun genClause(input: String): String {
        if (key.isEmpty()) return ""
        if (input.isEmpty()) return ""
        return "$key like '%%%$input%%'"
    }

    fun tokenizePossibleInputs(input: String): List<String> {
        if (input.isEmpty()) return emptyList()
        return input
                .replace("!", "")
                .replace("@", "")
                .replace("#", "")
                .replace("$", "")
                .replace("%", "")
                .replace("^", "")
                .replace("&", "")
                .replace("&", "")
                .replace("*", "")
                .replace("(", "")
                .replace(")", "")
                .replace(":", "")
                .replace(";", "")
                .replace("[", "")
                .replace("]", "")
                .replace(".", "")
                .replace(",", "")
                .replace("/", "")
                .replace("\\", "")
                .trim()
                .split(" ")
                .map(String::trim)
                .filter { !it.isEmpty() }
    }

    //endregion

}