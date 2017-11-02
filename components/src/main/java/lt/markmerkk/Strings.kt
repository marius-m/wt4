package lt.markmerkk

/**
 * Provides translations and strings
 */
interface Strings {
    /**
     * Provides string translation.
     * If translation is not provided. Will output key as output and report it.
     */
    fun getString(stringKey: String): String
}
