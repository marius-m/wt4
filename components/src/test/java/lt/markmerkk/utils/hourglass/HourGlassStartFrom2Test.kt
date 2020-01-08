package lt.markmerkk.utils.hourglass

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.WTEventBus
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HourGlassStartFrom2Test {

    @Mock lateinit var eventBus: WTEventBus
    private lateinit var hourGlass: HourGlass

    private val timeProvider = TimeProviderTest()


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        hourGlass = HourGlass(eventBus, timeProvider)
    }

    @Test
    fun valid() {
        // Assemble
        val now = timeProvider.now()

        // Act
        hourGlass.startFrom(now.minusMinutes(5))
        val duration = hourGlass.duration
        val start = hourGlass.start

        // Assert
        assertThat(start).isEqualTo(now.minusMinutes(5))
        assertThat(duration).isEqualTo(
                Duration(
                        now,
                        now.plusMinutes(5)
                )
        )
    }

    @Test
    fun startOverNow() {
        // Assemble
        val now = timeProvider.now()

        // Act
        hourGlass.startFrom(now.plusMinutes(5))
        val duration = hourGlass.duration
        val start = hourGlass.start

        // Assert
        assertThat(start).isEqualTo(now)
        assertThat(duration).isEqualTo(
                Duration(now, now)
        )
    }
}