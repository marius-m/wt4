package lt.markmerkk.ui.day

import jfxtras.internal.scene.control.skin.agenda.AgendaDaySkin
import jfxtras.internal.scene.control.skin.agenda.base24hour.AgendaSkinTimeScale24HourAbstract
import jfxtras.scene.control.agenda.Agenda
import org.joda.time.DateTime

import java.time.LocalDate

class CustomAgendaDayView(
        control: Agenda,
        private val targetDate: DateTime
) : AgendaSkinTimeScale24HourAbstract<AgendaDaySkin>(control) {

    override fun determineDisplayedLocalDates(): List<LocalDate> {
//        val targetDateToLocalDate = LocalDate.now()
//                .withYear(targetDate.year)
//                .withMonth(targetDate.monthOfYear)
//                .withDayOfMonth(targetDate.dayOfMonth)
        // todo : incorrect time
        return listOf(LocalDate.now())
    }

}
