package lt.markmerkk.datepick

enum class DateSelectType {
    UNKNOWN,

    /**
     * Date selection what date to display
     */
    TARGET_DATE,

    /**
     * Date selection to select in time range
     */
    SELECT_FROM,

    /**
     * Date selection to select in time range
     */
    SELECT_TO,
    ;

    companion object {
        fun fromRaw(raw: String): DateSelectType {
            return values()
                .firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
        }
    }
}