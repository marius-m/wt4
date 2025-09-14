package lt.markmerkk

import lt.markmerkk.clientextension.JiraClientExt
import lt.markmerkk.exceptions.AuthException

interface JiraClientProvider {

    fun markAsError()

    @Throws(AuthException::class)
    fun newClient(): JiraClientExt

    @Throws(AuthException::class)
    fun client(): JiraClientExt

    fun hasError(): Boolean

    fun hostname(): String

    fun username(): String

}