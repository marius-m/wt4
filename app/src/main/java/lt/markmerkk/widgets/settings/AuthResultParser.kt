package lt.markmerkk.widgets.settings

// todo incomplete behaviour display
class AuthResultParser {

    private val regexToken = "Your verification code is '([0-9A-Za-z]+)'\\."
            .toRegex()

    // todo incomplete behaviour
    fun findAccessToken(inputResponse: String): String {
        return regexToken.find(inputResponse)?.groupValues!![1] ?: ""
    }

}