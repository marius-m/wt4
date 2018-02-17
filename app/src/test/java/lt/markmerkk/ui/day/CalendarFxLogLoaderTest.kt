package lt.markmerkk.ui.day

import com.calendarfx.model.Entry
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
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
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        loader.load(
                listOf(createSimpleLog())
        )

        // Assert
        val calendarEntryCaptor = argumentCaptor<List<Entry<String>>>()
        verify(view).onCalendarEntries(
                calendarEntryCaptor.capture()
        )
        assertThat(calendarEntryCaptor.firstValue).hasSize(1)
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