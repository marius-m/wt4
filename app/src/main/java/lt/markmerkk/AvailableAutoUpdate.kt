package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
object AvailableAutoUpdate {
    val values = listOf(
            AutoUpdateValue(
                    -1,
                    Translation.getInstance().getString("settings_autorefresh_check_disabled")
            ),
            AutoUpdateValue(
                    1,
                    Translation.getInstance().getString("settings_autorefresh_check_time")
            ),
            AutoUpdateValue(
                    5,
                    Translation.getInstance().getString("settings_autorefresh_check_time")
            ),
            AutoUpdateValue(
                    10,
                    Translation.getInstance().getString("settings_autorefresh_check_time")
            ),
            AutoUpdateValue(
                    30,
                    Translation.getInstance().getString("settings_autorefresh_check_time")
            ),
            AutoUpdateValue(
                    60,
                    Translation.getInstance().getString("settings_autorefresh_check_time")
            ),
            AutoUpdateValue(
                    120,
                    Translation.getInstance().getString("settings_autorefresh_check_time")
            )
    )

    fun findAutoUpdateValueByMinute(minute: Int): AutoUpdateValue {
        val foundValue = values.find { it.timeoutMinutes == minute }
        if (foundValue != null) return foundValue
        return values[0]
    }

}