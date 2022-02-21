package lt.markmerkk.validators

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderJfx
import lt.markmerkk.WorklogStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LogChangeValidatorCanEditTest {

    @Mock lateinit var worklogStorage: WorklogStorage
    private lateinit var logChangeValidator: LogChangeValidator

    private val timeProvider = TimeProviderJfx()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        logChangeValidator = LogChangeValidator(worklogStorage)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Mocks.createLog(timeProvider))
            .whenever(worklogStorage).findById(any())

        // Act
        val resultCanEdit = logChangeValidator.canEditSimpleLog(logLocalId = 1L)

        // Assert
        assertThat(resultCanEdit).isTrue()
    }

    @Test
    fun logDoesNotExist() {
        // Assemble
        doReturn(null).whenever(worklogStorage).findById(any())

        // Act
        val resultCanEdit = logChangeValidator.canEditSimpleLog(logLocalId = 1L)

        // Assert
        assertThat(resultCanEdit).isFalse()
    }

}