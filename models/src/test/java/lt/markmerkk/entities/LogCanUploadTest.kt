package lt.markmerkk.entities

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogCanUploadTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun localLog() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-66",
                comment = "valid_comment",
                remoteData = null
        )

        val resultCanUpload = log.canUpload

        assertThat(resultCanUpload).isTrue()
    }

    @Test
    fun noComment() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-66",
                comment = "",
                remoteData = null
        )

        val resultCanUpload = log.canUpload

        assertThat(resultCanUpload).isFalse()
    }

    @Test
    fun notValidTicket() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now.plusMinutes(5),
                code = "invalid_ticket",
                comment = "valid_comment",
                remoteData = null
        )

        val resultCanUpload = log.canUpload

        assertThat(resultCanUpload).isFalse()
    }

    @Test
    fun durationLessThanOneMinute() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now,
                code = "WT-66",
                comment = "valid_comment",
                remoteData = null
        )

        val resultCanUpload = log.canUpload

        assertThat(resultCanUpload).isFalse()
    }

    @Test
    fun remoteLog() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-66",
                comment = "valid_comment",
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        )

        val resultCanUpload = log.canUpload

        assertThat(resultCanUpload).isFalse()
    }
}