package lt.markmerkk.ui.day

import com.calendarfx.model.Entry
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.utils.CalendarFxLogLoader
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers

class CalendarFxLogLoaderTest {

    @Mock lateinit var view: CalendarFxLogLoader.View
    lateinit var loader: CalendarFxLogLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = CalendarFxLogLoader(
                view,
                TimeProviderTest(),
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        loader.load(listOf(createSimpleLog()))

        // Assert
        val calendarEntryCaptor = argumentCaptor<List<Entry<SimpleLog>>>()
        verify(view).onCalendarEntries(
                calendarEntryCaptor.capture(),
                any(),
                any(),
                any()
        )
        assertThat(calendarEntryCaptor.firstValue).hasSize(1)
    }

    @Test
    fun noEntries() {
        // Assemble
        // Act
        loader.load(emptyList())

        // Assert
        verify(view).onCalendarNoEntries()
    }

    //region Mocks

    fun createSimpleLog(): SimpleLog {
        val simpleLog: SimpleLog = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setComment("valid_comment")
                .build()
        return simpleLog
    }

    //endregion

}