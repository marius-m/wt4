package lt.markmerkk.mvp

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
     * Triggers to show debug data
     */
    fun debug()

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
        fun showAuthSuccess()

        /**
         * Shows that auth has failed when trying to validate login
         */
        fun showAuthFailUnauthorised(error: Throwable)

        /**
         * Shows that auth has failed when trying to validate login
         */
        fun showAuthFailInvalidHostname(error: Throwable)

        /**
         * Shows that auth has failed when trying to validate login
         */
        fun showAuthFailInvalidUndefined(error: Throwable)

        /**
         * Shows logs for auth troubleshooting
         */
        fun showDebugLogs()

        /**
         * Hides logs for auth troubleshooting.
         * Should show the undefined state of login data.
         */
        fun hideDebugLogs()
    }

    interface AuthInteractor {
        fun jiraTestValidConnection(
                hostname: String,
                username: String,
                password: String
        ): Observable<Boolean>
    }

}