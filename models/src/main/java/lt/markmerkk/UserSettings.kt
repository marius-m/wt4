package lt.markmerkk

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
    var ticketLastUpdate: Long
}