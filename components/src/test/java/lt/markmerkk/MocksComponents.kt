package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime
import org.joda.time.Duration

object MocksComponents {
    fun createHourGlass(
        timeProvider: TimeProvider,
        start: DateTime = timeProvider.now(),
        end: DateTime = timeProvider.now(),
        isRunning: Boolean = false,
    ): HourGlass {
        val hourGlass: HourGlass = mock()
        val duration = Duration(start, end)
        doReturn(start).whenever(hourGlass).start
        doReturn(end).whenever(hourGlass).end
        doReturn(duration).whenever(hourGlass).duration
        doReturn(isRunning).whenever(hourGlass).isRunning()
        return hourGlass
    }
}