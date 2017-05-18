package lt.markmerkk.ui.week

import jfxtras.scene.control.agenda.Agenda
import lt.markmerkk.entities.SimpleLog
import org.joda.time.DateTime
import rx.Observable
import rx.Scheduler
import rx.Single
import rx.Subscription
import java.time.LocalDateTime

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 * Responsible for loading agenda asynchronously
 */
@Deprecated(message = "Use AgendaPresenterImpl2 instead")
class AgendaPresenterImpl(
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
        subscription = reloadObservable(logs)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    println("Success loading appointments!")
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

    //region Observables

    fun reloadObservable(logs: List<SimpleLog>): Single<List<Agenda.AppointmentImplLocal>> {
        return Observable.from(logs)
               .flatMap {
                    val startTime = DateTime(it.start)
                    val endTime = DateTime(it.end)
                    var appointmentGroup: Agenda.AppointmentGroup? = null
                    when (it.stateImageUrl) {
                        "/red.png" -> appointmentGroup = appointmentGroupRed
                        "/yellow.png" -> appointmentGroup = appointmentGroupYellow
                        "/green.png" -> appointmentGroup = appointmentGroupGreen
                    }
                    Observable.just(
                            AppointmentSimpleLog(it)
                                    .withStartLocalDateTime(
                                            LocalDateTime.of(
                                                    startTime.year,
                                                    startTime.monthOfYear,
                                                    startTime.dayOfMonth,
                                                    startTime.hourOfDay,
                                                    startTime.minuteOfHour)
                                    )
                                    .withEndLocalDateTime(
                                            LocalDateTime.of(
                                                    endTime.year,
                                                    endTime.monthOfYear,
                                                    endTime.dayOfMonth,
                                                    endTime.hourOfDay,
                                                    endTime.minuteOfHour)
                                    )
                                    .withAppointmentGroup(appointmentGroup)
                                    .withSummary(it.task + "\n" + it.comment)
                    )
                }
                .reduce(
                        mutableListOf<Agenda.AppointmentImplLocal>(),
                        { accumulator, next ->
                            accumulator.add(next)
                            accumulator
                        }
                )
                .map { it.toList() }
                .toSingle()
    }
    //endregion

}