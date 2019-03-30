package lt.markmerkk.ui.week

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import jfxtras.scene.control.agenda.Agenda
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-09
 */
class AgendaPresenterImplReloadTest {
    val agendaView: AgendaView = mock()
    val appointmentGroup: Agenda.AppointmentGroup = mock()
    val agendaPresenter = AgendaPresenterImpl(
            agendaView,
            appointmentGroup,
            appointmentGroup,
            appointmentGroup,
            Schedulers.immediate(),
            Schedulers.immediate()
    )

    @Test
    fun emptyList_noReferesh() {
        agendaPresenter.reloadView(emptyList())

        verify(agendaView).updateAgenda(anyList())
    }

    @Test
    fun reloadObservable_emptyList_noValues() {
        val testSubscriber = TestSubscriber<List<Agenda.AppointmentImplLocal>>()
        agendaPresenter.reloadObservable(emptyList<SimpleLog>())
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val results = testSubscriber.onNextEvents.first()
        assertEquals(0, results.size)
    }

    @Test
    fun reloadObservable_validLocalValue_validValue() {
        val testSubscriber = TestSubscriber<List<Agenda.AppointmentImplLocal>>()
        agendaPresenter.reloadObservable(
                listOf(
                        SimpleLogBuilder(1000)
                                .setStart(1000)
                                .setEnd(2000)
                                .setComment("fake_comment")
                                .build()
                )
        ).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val results = testSubscriber.onNextEvents.first()
        assertEquals(1, results.size)
    }
}