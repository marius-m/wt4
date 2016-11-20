package lt.markmerkk.utils.graphs

import javafx.util.StringConverter
import lt.markmerkk.utils.LogUtils

/**
 * @author mariusmerkevicius
 * @since 2016-11-19
 */
class TimeSpentAxisFormatter() : StringConverter<Number>() {
    override fun toString(obj: Number): String {
        return LogUtils.formatDuration(obj.toLong())
    }

    override fun fromString(string: String): Number {
        throw UnsupportedOperationException("Cant recover from formatted spent time")
    }

}