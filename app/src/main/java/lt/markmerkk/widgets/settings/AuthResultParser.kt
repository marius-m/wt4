package lt.markmerkk.widgets.settings

/**
 * Responsible for parsing auth auth token from webview
 */
class AuthResultParser {

    private val regexToken = "Your verification code is '([0-9A-Za-z]+)'\\."
            .toRegex()

    fun findAccessToken(inputResponse: String): String {
        val matchResult = regexToken.find(inputResponse) ?: return ""
        if (matchResult.groupValues.size < 2) {
            return ""
        }
        return matchResult.groupValues[1]
    }

}