package lt.markmerkk.timeselect.entities

enum class TimeSelectType {
    UNKNOWN,
    FROM,
    TO,
    ;

    companion object {
        fun fromRaw(raw: String): TimeSelectType {
            return values()
                .firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
        }
    }
}