package lt.markmerkk.mvp

import java.time.LocalDateTime

class TimeQuickModifierImpl(
        private val listener: TimeQuickModifier.Listener
) : TimeQuickModifier {
    override fun subtractStartTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    ) {
        listener.onTimeModified(
                startDateTime.minusMinutes(1),
                endDateTime
        )
    }

    override fun appendStartTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    ) {
        val newStartTime = startDateTime.plusMinutes(1)
        if (newStartTime.isAfter(endDateTime)) {
            listener.onTimeModified(
                    startDateTime,
                    endDateTime
            )
            return
        }
        listener.onTimeModified(
                newStartTime,
                endDateTime
        )
    }

    override fun subtractEndTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    ) {
        val newEndTime = endDateTime.minusMinutes(1)
        if (newEndTime.isBefore(startDateTime)) {
            listener.onTimeModified(
                    startDateTime,
                    endDateTime
            )
            return
        }
        listener.onTimeModified(
                startDateTime,
                newEndTime
        )
    }

    override fun appendEndTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    ) {
        listener.onTimeModified(
                startDateTime,
                endDateTime.plusMinutes(1)
        )
    }
}