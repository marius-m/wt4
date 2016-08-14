package lt.markmerkk.utils

import lt.markmerkk.Const
import lt.markmerkk.UserSettings
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by mariusmerkevicius on 12/21/15.
 * Controller for holding persistent data
 */
class UserSettingsImpl(
        private val settings: HashSettings
) : UserSettings {

    override fun onAttach() {
        settings.load()
        host = settings.get(HOST) ?: ""
        username = settings.get(USER) ?: ""
        password = settings.get(PASS) ?: ""
        val versionString = settings.get(VERSION)
        if (versionString != null) {
            try {
                version = Integer.parseInt(versionString)
            } catch (e: NumberFormatException) {
                version = -1
            }
        } else {
            version = -1
        }
        val autoUpdateString = settings.get(AUTOUPDATE_TIMEOUT)
        if (autoUpdateString != null) {
            try {
                autoUpdateMinutes = Integer.parseInt(autoUpdateString)
            } catch (e: NumberFormatException) {
                autoUpdateMinutes = -1
            }
        } else {
            autoUpdateMinutes = -1
        }
        val lastUpdateString = settings.get(LAST_UPDATE)
        if (lastUpdateString != null) {
            try {
                lastUpdate = lastUpdateString.toLong()
            } catch (e: NumberFormatException) {
                lastUpdate = -1
            }
        } else {
            lastUpdate = -1
        }
        issueJql = settings.get(ISSUE_JQL) ?: Const.DEFAULT_JQL_USER_ISSUES
    }

    override fun onDetach() {
        settings.set(HOST, host)
        settings.set(USER, username)
        settings.set(PASS, password)
        settings.set(VERSION, version.toString())
        settings.set(ISSUE_JQL, issueJql)
        settings.set(AUTOUPDATE_TIMEOUT, autoUpdateMinutes.toString())
        settings.set(LAST_UPDATE, lastUpdate.toString())
        settings.save()
    }

    override var host: String = ""

    override var username: String = ""

    override var password: String = ""

    override var issueJql: String = Const.DEFAULT_JQL_USER_ISSUES

    override var version = -1

    override var autoUpdateMinutes: Int = -1

    override var lastUpdate: Long = -1

    //region Getters / Setters

    override fun setCustom(key: String, value: String) {
        settings.set(key, value)
        settings.save()
    }

    override fun getCustom(key: String): String? {
        return settings.get(key)
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(UserSettingsImpl::class.java)!!

        val HOST = "HOST"
        val USER = "USER"
        val PASS = "PASS"
        val VERSION = "VERSION"
        val ISSUE_JQL = "ISSUE_JQL"
        val AUTOUPDATE_TIMEOUT = "AUTOUPDATE_TIMEOUT"
        val LAST_UPDATE = "LAST_UPDATE"
    }
}
