package lt.markmerkk.entities

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SyncStatusExposeStatusTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun waitingForSync() {
        // Assemble
        val defaultLog = Mocks.createBasicLog(timeProvider)

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.WAITING_FOR_SYNC)
    }

    @Test
    fun inSync() {
        // Assemble
        val defaultLog = Mocks.createBasicLogRemote(
                timeProvider,
                remoteId = 1L
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.IN_SYNC)
    }

    @Test
    fun isError() {
        // Assemble
        val defaultLog = Mocks.createBasicLogRemote(
                timeProvider,
                remoteId = 0L,
                errorMessage = "error",
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.ERROR)
    }
}