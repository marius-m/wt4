package lt.markmerkk.mvp

import org.joda.time.DateTime

class TimeQuickModifierImpl(
        private val listener: TimeQuickModifier.Listener
) : TimeQuickModifier {
    override fun subtractStartTime(
            start: DateTime,
            end: DateTime
    ) {
        listener.onTimeModified(
                start.minusMinutes(1),
                end
        )
    }

    override fun appendStartTime(
            start: DateTime,
            end: DateTime
    ) {
        val newStartTime = start.plusMinutes(1)
        if (newStartTime.isAfter(end)) {
            listener.onTimeModified(
                    start,
                    end
            )
            return
        }
        listener.onTimeModified(
                newStartTime,
                end
        )
    }

    override fun subtractEndTime(
            start: DateTime,
            end: DateTime
    ) {
        val newEndTime = end.minusMinutes(1)
        if (newEndTime.isBefore(start)) {
            listener.onTimeModified(
                    start,
                    end
            )
            return
        }
        listener.onTimeModified(
                start,
                newEndTime
        )
    }

    override fun appendEndTime(
            start: DateTime,
            end: DateTime
    ) {
        listener.onTimeModified(
                start,
                end.plusMinutes(1)
        )
    }
}