package lt.markmerkk.utils

import java.util.HashMap
import java.util.regex.Pattern

/**
 * Created by mariusmerkevicius on 2/16/16.
 * Responsible for splitting search phrase for more accurate results
 * traversing local database
 */
class IssueSplit {

    fun split(inputPhrase: String): Map<String, String> {
        val map = HashMap<String, String>()
        map.put(DESCRIPTION_KEY, pickPart(inputPhrase, DESCRIPTION_REGEX))
        map.put(KEY_KEY, pickPart(inputPhrase, KEY_REGEX))
        return map
    }

    //region Convenience

    /**
     * Will pick part of message depending on regex
     * @param inputPhrase
     * *
     * @return
     */
    fun pickPart(inputPhrase: String, regex: String): String {
        if (inputPhrase.isNullOrEmpty()) return ""
        var description = pickMessage(inputPhrase, regex)
        if (description == null)
            description = inputPhrase
        return description.trim { it <= ' ' }
    }

    companion object {
        val SEPERATOR = ":"
        val VALID_MESSAGE_SEPARATORS = arrayOf(SEPERATOR)

        val KEY_KEY = "KEY_KEY"
        val KEY_REGEX = "^.+($SEPERATOR)"
        val DESCRIPTION_KEY = "DESCRIPTION_KEY"
        val DESCRIPTION_REGEX = "($SEPERATOR).+$"

        /**
         * Picks one part of messge, depending on provided regex

         * @param message provided comment end parse
         * *
         * @param regex provided regex end use
         * *
         * @return picked comment part
         */
        fun pickMessage(message: String, regex: String?): String? {
            var rawMessage = message
            if (regex == null)
                throw IllegalArgumentException("Regex cannot be null!")
            if (rawMessage.isNullOrEmpty())
                return null
            // Cleaning line breaks
            rawMessage = rawMessage.replace("\\n".toRegex(), "")
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(rawMessage.trim { it <= ' ' })
            if (matcher.find()) {
                var found = matcher.group()
                found = cleanSeparators(found, VALID_MESSAGE_SEPARATORS)
                found = found.trim { it <= ' ' }
                if (found.length == 0)
                    return null
                return found
            }
            return null
        }

        /**
         * Clears all valid separators

         * @param input input comment
         * *
         * @return comment without separators
         */
        fun cleanSeparators(input: String, separators: Array<String>): String {
            var rawInput = input
            for (validSeparator in separators)
                rawInput = rawInput.replace(validSeparator, "")
            return rawInput
        }
    }

    //endregion

}
