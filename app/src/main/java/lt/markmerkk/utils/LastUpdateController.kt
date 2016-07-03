package lt.markmerkk.utils

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
interface LastUpdateController {
    var loading: Boolean
    var error: String?
    val output: String
    fun refresh()
    fun durationTillLastUpdate(): Long
}