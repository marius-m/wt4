package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
data class Config(
        val debug: Boolean = true,
        val versionName: String = "Undefined",
        val versionCode: Int = -1,
        val gaKey: String
) {
    override fun toString(): String {
        return "Config: DEBUG=$debug; versionName=$versionName; versionCode=$versionCode; gaKey=$gaKey"
    }
}