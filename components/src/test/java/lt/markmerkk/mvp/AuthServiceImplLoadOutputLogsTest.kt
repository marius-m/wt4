package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthServiceImplLoadOutputLogsTest : AbsAuthServiceImplTest() {

    @Test
    fun validFileLoad() {
        // Assemble
        doReturn("valid_log").whenever(logLoader).loadLastLogs(any(), any())

        // Act
        serviceWithTestSchedulers.loadOutputLogs()
        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)

        // Assert
        verify(view).fillDebugLogs(any())
    }

    @Test
    fun errorLoading() {
        // Assemble
        doThrow(RuntimeException()).whenever(logLoader).loadLastLogs(any(), any())

        // Act
        serviceWithTestSchedulers.loadOutputLogs()
        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)

        // Assert
        verify(view).errorFillingDebugLogs(any())
    }

    @Test
    fun valid_recurringLoad() {
        // Assemble
        doReturn("valid_log").whenever(logLoader).loadLastLogs(any(), any())

        // Act
        serviceWithTestSchedulers.loadOutputLogs()
        serviceWithTestSchedulers.loadOutputLogs()
        serviceWithTestSchedulers.loadOutputLogs()
        serviceWithTestSchedulers.loadOutputLogs()
        serviceWithTestSchedulers.loadOutputLogs()
        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS) // Should only load once

        // Assert
        verify(view).fillDebugLogs(any())
    }

}