package lt.markmerkk.utils

import lt.markmerkk.Const
import lt.markmerkk.UserSettings

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
        issueJql = settings.get(ISSUE_JQL) ?: Const.DEFAULT_JQL_USER_ISSUES
    }

    override fun onDetach() {
        settings.set(HOST, host)
        settings.set(USER, username)
        settings.set(PASS, password)
        settings.set(VERSION, version.toString())
        settings.set(ISSUE_JQL, issueJql)
        settings.save()
    }

    override var host: String = ""
        set(value) {
            field = value
            settings.save()
        }
    override var username: String = ""
        set(value) {
            field = value
            settings.save()
        }
    override var password: String = ""
        set(value) {
            field = value
            settings.save()
        }
    override var issueJql: String = Const.DEFAULT_JQL_USER_ISSUES
        set(value) {
            field = value
            settings.save()
        }

    override var version = -1
        set(value) {
            field = value
            settings.save()
        }

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
        val HOST = "HOST"
        val USER = "USER"
        val PASS = "PASS"
        val VERSION = "VERSION"
        val ISSUE_JQL = "ISSUE_JQL"
    }
}
