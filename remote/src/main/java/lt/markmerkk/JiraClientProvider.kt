package lt.markmerkk

import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.RestClient

interface JiraClientProvider {

    fun markAsError()

    @Throws(AuthException::class)
    fun newClient(): JiraClient

    @Throws(AuthException::class)
    fun client(): JiraClient
}