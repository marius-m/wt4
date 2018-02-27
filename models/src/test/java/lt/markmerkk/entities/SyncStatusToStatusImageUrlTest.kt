package lt.markmerkk.entities

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class SyncStatusToStatusImageUrlTest {

    @Test
    fun notAllCasesCovered() {
        // Assemble
        // Act
        // Assert
        SyncStatus.values().forEach {
            try {
                SyncStatus.toStatusImageUrl(it)
            } catch (e: Exception) {
                fail("Not all cases covered!")
            }
        }
    }

    @Test
    fun inSync() {
        // Assemble
        // Act
        val result = SyncStatus.toStatusImageUrl(SyncStatus.IN_SYNC)

        // Assert
        assertThat(result).isEqualTo("/green.png")
    }

    @Test
    fun waitingForSync() {
        // Assemble
        // Act
        val result = SyncStatus.toStatusImageUrl(SyncStatus.WAITING_FOR_SYNC)

        // Assert
        assertThat(result).isEqualTo("/yellow.png")
    }

    @Test
    fun error() {
        // Assemble
        // Act
        val result = SyncStatus.toStatusImageUrl(SyncStatus.ERROR)

        // Assert
        assertThat(result).isEqualTo("/red.png")
    }

    @Test
    fun invalid() {
        // Assemble
        // Act
        val result = SyncStatus.toStatusImageUrl(SyncStatus.INVALID)

        // Assert
        assertThat(result).isEqualTo("/gray.png")
    }

}