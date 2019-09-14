package lt.markmerkk.interactors

import lt.markmerkk.JiraUser
import rx.Observable
import rx.Single

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
    }

    interface AuthInteractor {
        fun jiraTestValidConnection(
                hostname: String,
                username: String,
                password: String
        ): Single<JiraUser>
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

}