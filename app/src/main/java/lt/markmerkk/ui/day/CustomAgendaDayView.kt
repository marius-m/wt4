package lt.markmerkk.ui.day

import jfxtras.internal.scene.control.skin.agenda.AgendaDaySkin
import jfxtras.internal.scene.control.skin.agenda.base24hour.AgendaSkinTimeScale24HourAbstract
import jfxtras.scene.control.agenda.Agenda
import lt.markmerkk.utils.DateCompat
import org.joda.time.DateTime

import java.time.LocalDate

class CustomAgendaDayView(
        control: Agenda
) : AgendaSkinTimeScale24HourAbstract<AgendaDaySkin>(control) {

    private var targetDate: LocalDate = LocalDate.now()

    override fun determineDisplayedLocalDates(): List<LocalDate> {
        if (targetDate == null) {
            return listOf(LocalDate.now())
        }
        return listOf(targetDate)
    }

    fun refreshWithDate(dateTime: DateTime) {
        targetDate = DateCompat.toJavaLocalDate(dateTime)
        refresh()
    }

}
