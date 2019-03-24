package lt.markmerkk.utils

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 */
interface IssueSplit {
    fun split(inputPhrase: String): Map<String, String>
    fun pickPart(inputPhrase: String, regex: String): String
}