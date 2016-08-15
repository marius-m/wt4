package lt.markmerkk.entities

/**
 * @author mariusmerkevicius
 * @since 2016-08-15
 */
interface VersionSummary<out T> {
    fun get(): T
}