package lt.markmerkk.utils

interface ConfigSetSettings {
    /**
     * Changes active config
     */
    fun changeActiveConfig(configSelection: String)
    /**
     * @return Current config value or empty
     */
    fun currentConfig(): String

    /**
     * @return current config or 'default' value
     */
    fun currentConfigOrDefault(): String

    /**
     * @return List of available configs
     */
    fun configs(): List<String>
    fun load()
    fun save()
}