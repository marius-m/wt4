package lt.markmerkk

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

    override fun genClauses(input: String): List<String> {
        return tokenizePossibleInputs(input)
                .map { genClause(it) }
    }

    override fun genQuery(clauses: List<String>): String {
        val filterClauses = clauses.filter { !it.isEmpty() }
        if (filterClauses.isEmpty()) return ""
        val separator = " OR "
        val sb = StringBuilder()
        sb.append("(")
        filterClauses.forEach {
            sb.append(it)
            sb.append(separator)
        }
        sb.delete(sb.length - separator.length, sb.length)
        sb.append(")")
        return sb.toString()
    }

    //region Convenience

    /**
     * Generates valid 'like' clause
     */
    fun genClause(input: String): String {
        if (input.isEmpty()) return ""
        return "$key like '%%$input%%'"
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