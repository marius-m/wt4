package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-08-13
 */
data class AutoUpdateValue(
        val timeoutMinutes: Int,
        val stringRepresentation: String
) {
    override fun toString(): String {
        return String.format(stringRepresentation, timeoutMinutes)
    }
}