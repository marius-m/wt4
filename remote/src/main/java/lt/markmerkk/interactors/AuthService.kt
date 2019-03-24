package lt.markmerkk.interactors

import rx.Observable

/**
 * Responsible for checking and debugging the login for JIRA auth
 */
interface AuthService {

    fun onAttach()
    fun onDetach()

    /**
     * Triggers to test login data
     */
    fun testLogin(
            hostname: String,
            username: String,
            password: String
    )

    /**
     * Triggers to show debug console / visual mode
     */
    fun toggleDisplayType()

    fun logDisplayType(): LogDisplayType

    interface View {
        /**
         * Shows loading indicator
         */
        fun showProgress()

        /**
         * Hides loading indicator
         */
        fun hideProgress()

        /**
         * Shows auth is valid
         */
        fun showAuthResult(result: AuthResult)

        /**
         * Shows logs for auth troubleshooting
         */
        fun showDebugLogs()

        /**
         * Hides logs for auth troubleshooting.
         * Should show the undefined state of login data.
         */
        fun hideDebugLogs()

        /**
         * Fills debug logs
         */
        fun fillDebugLogs(logs: String)

        /**
         * Point an error when filling debug logs
         */
        fun errorFillingDebugLogs(throwable: Throwable)

        /**
         * Scroll to bottom of debug logs
         */
        fun scrollToBottomOfDebugLogs(length: Int)
    }

    interface AuthInteractor {
        fun jiraTestValidConnection(
                hostname: String,
                username: String,
                password: String
        ): Observable<Boolean>
    }

    /**
     * Authorisation check result
     */
    enum class AuthResult {
        UNKNOWN,
        SUCCESS,
        ERROR_EMPTY_FIELDS,
        ERROR_UNAUTHORISED,
        ERROR_INVALID_HOSTNAME,
        ERROR_UNDEFINED,
    }

    enum class LogDisplayType {
        VISUAL,  // Displays cool graphic icon, indicating log status
        TEXT,    // Displays informative debug log
    }

}