package lt.markmerkk.ui.week

import jfxtras.scene.control.agenda.Agenda
import lt.markmerkk.entities.SimpleLog
import org.joda.time.DateTime
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.time.LocalDateTime

class AgendaPresenterImpl2(
        var agendaView: AgendaView?,
        val appointmentGroupRed: Agenda.AppointmentGroup,
        val appointmentGroupYellow: Agenda.AppointmentGroup,
        val appointmentGroupGreen: Agenda.AppointmentGroup,
        val ioScheduler: Scheduler,
        val uiScheduler: Scheduler
) : AgendaPresenter {

    var subscription: Subscription? = null

    override fun reloadView(logs: List<SimpleLog>) {
        subscription?.unsubscribe()
        subscription = Observable.from(logs)
                .subscribeOn(ioScheduler)
                .flatMap { Observable.just(toAppointmentSimpleLog(it)) }
                .toList()
                .observeOn(uiScheduler)
                .subscribe({
                    agendaView?.updateAgenda(it)
                }, {
                    println("Error reloading view: $it")
                }).apply { subscription = this }
    }

    override fun onAttach() { }

    override fun onDetatch() {
        subscription?.unsubscribe()
        agendaView = null
    }

    //region Convenience

    fun toAppointmentSimpleLog(simpleLog: SimpleLog): AppointmentSimpleLog {
        val startTime = DateTime(simpleLog.start)
        val endTime = DateTime(simpleLog.end)
        var appointmentGroup: Agenda.AppointmentGroup? = null
        when (simpleLog.stateImageUrl) {
            "/red.png" -> appointmentGroup = appointmentGroupRed
            "/yellow.png" -> appointmentGroup = appointmentGroupYellow
            "/green.png" -> appointmentGroup = appointmentGroupGreen
        }
        return AppointmentSimpleLog(simpleLog)
                .withStartLocalDateTime(
                        LocalDateTime.of(
                                startTime.year,
                                startTime.monthOfYear,
                                startTime.dayOfMonth,
                                startTime.hourOfDay,
                                startTime.minuteOfHour
                        )
                )
                .withEndLocalDateTime(
                        LocalDateTime.of(
                                endTime.year,
                                endTime.monthOfYear,
                                endTime.dayOfMonth,
                                endTime.hourOfDay,
                                endTime.minuteOfHour
                        )
                )
                .withAppointmentGroup(appointmentGroup)
                .withSummary(simpleLog.comment) as AppointmentSimpleLog
    }

    //endregion

}