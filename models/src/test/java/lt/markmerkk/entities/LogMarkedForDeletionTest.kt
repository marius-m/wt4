package lt.markmerkk.entities

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogMarkedForDeletionTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun localLog() {
        val resultIsMarkedForDeletion = Mocks.createLog(timeProvider, id = 1)
                .isMarkedForDeletion
        assertThat(resultIsMarkedForDeletion).isFalse()
    }

    @Test
    fun remoteLog() {
        val resultIsMarkedForDeletion = Mocks.createLog(
                timeProvider, id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        ).isMarkedForDeletion
        assertThat(resultIsMarkedForDeletion).isFalse()
    }

    @Test
    fun remoteLog_markedForDeletion() {
        val resultIsMarkedForDeletion = Mocks.createLog(
                timeProvider, id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2,
                        isDeleted = true
                )
        ).isMarkedForDeletion
        assertThat(resultIsMarkedForDeletion).isTrue()
    }
}