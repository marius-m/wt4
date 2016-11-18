package lt.markmerkk.utils

/**
 * @author mariusmerkevicius
 * @since 2016-11-07
 */
interface ConfigSetSettings {
    var configSetName: String
    var configs: List<String>

    fun load()
    fun save()
}