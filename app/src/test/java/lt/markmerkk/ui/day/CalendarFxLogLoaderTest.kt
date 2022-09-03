package lt.markmerkk.ui.day

import com.calendarfx.model.Entry
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.Log
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

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = CalendarFxLogLoader(
                view,
                timeProvider,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        loader.load(listOf(Mocks.createLog(timeProvider)))

        // Assert
        val calendarEntryCaptor = argumentCaptor<List<Entry<Log>>>()
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
}