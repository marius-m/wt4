package lt.markmerkk.validators

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.LogStorage
import lt.markmerkk.mvp.MocksLogEditService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LogChangeValidatorCanEditTest {

    @Mock lateinit var logStorage: LogStorage
    private lateinit var logChangeValidator: LogChangeValidator

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        logChangeValidator = LogChangeValidator(logStorage)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(MocksLogEditService.createValidLogWithDate()).whenever(logStorage).findByIdOrNull(any())

        // Act
        val resultCanEdit = logChangeValidator.canEditSimpleLog(simpleLogId = 1L)

        // Assert
        assertThat(resultCanEdit).isTrue()
    }

    @Test
    fun logDoesNotExist() {
        // Assemble
        doReturn(null).whenever(logStorage).findByIdOrNull(any())

        // Act
        val resultCanEdit = logChangeValidator.canEditSimpleLog(simpleLogId = 1L)

        // Assert
        assertThat(resultCanEdit).isFalse()
    }

    @Test
    fun logAlreadyUploaded() {
        // Assemble
        doReturn(MocksLogEditService.mockRemoteLog()).whenever(logStorage).findByIdOrNull(any())

        // Act
        val resultCanEdit = logChangeValidator.canEditSimpleLog(simpleLogId = 1L)

        // Assert
        assertThat(resultCanEdit).isFalse()
    }
}