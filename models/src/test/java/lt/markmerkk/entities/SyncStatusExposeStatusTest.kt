package lt.markmerkk.entities

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SyncStatusExposeStatusTest {

    @Test
    fun waitingForSync() {
        // Assemble
        val defaultLog = createSimpleLog(
                remoteId = 0,
                isDirty = true,
                isError = false
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.WAITING_FOR_SYNC)
    }

    @Test
    fun inSync() {
        // Assemble
        val defaultLog = createSimpleLog(
                remoteId = 1L,
                isDirty = false,
                isError = false
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.IN_SYNC)
    }

    @Test
    fun inSyncAndDirty() {
        // Assemble
        val defaultLog = createSimpleLog(
                remoteId = 1L,
                isDirty = true,
                isError = false
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.WAITING_FOR_SYNC)
    }

    @Test // this situation should not occur
    fun invalid_inSyncAndError() {
        // Assemble
        val defaultLog = createSimpleLog(
                remoteId = 1L,
                isDirty = false,
                isError = true
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.ERROR)
    }

    @Test
    fun isError() {
        // Assemble
        val defaultLog = createSimpleLog(
                remoteId = 0L,
                isDirty = false,
                isError = true
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.ERROR)
    }

    @Test
    fun errorAndDirty() {
        // Assemble
        val defaultLog = createSimpleLog(
                remoteId = 0L,
                isDirty = true,
                isError = true
        )

        // Act
        val result = SyncStatus.exposeStatus(defaultLog)

        // Assert
        assertThat(result).isEqualTo(SyncStatus.WAITING_FOR_SYNC)
    }

    //region Mocks

    fun createSimpleLog(
            remoteId: Long,
            isDirty: Boolean,
            isError: Boolean
    ): SimpleLog {
        val simpleLog: SimpleLog = SimpleLogBuilder(1000L).build()
        simpleLog.id = remoteId
        simpleLog.dirty = isDirty
        simpleLog.error = isError
        return simpleLog
    }

    //endregion

}