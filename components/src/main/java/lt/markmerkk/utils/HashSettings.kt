package lt.markmerkk.utils

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
interface HashSettings {
    fun save()
    fun load()
    fun set(key: String, value: String)
    fun get(key: String): String?
}