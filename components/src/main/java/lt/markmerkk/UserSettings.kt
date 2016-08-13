package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
interface UserSettings {
    fun onAttach()
    fun onDetach()

    var issueJql: String
    var host: String
    var username: String
    var password: String
    var version: Int
    var autoUpdateMinutes: Int
    var lastUpdate: Long
    fun setCustom(key: String, value: String)
    fun getCustom(key: String): String?
}