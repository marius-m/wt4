package lt.markmerkk.utils

interface ConfigSetSettings {
    var configSetName: String
    var configs: List<String>

    fun currentConfigOrDefault(): String
    fun load()
    fun save()
}