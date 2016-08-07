package lt.markmerkk.mvp

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
interface UserSettings {
    var issueJql: String
    var host: String
    var username: String
    var password: String
    var version: Int
    fun setCustom(key: String, value: String)
    fun getCustom(key: String): String?
}