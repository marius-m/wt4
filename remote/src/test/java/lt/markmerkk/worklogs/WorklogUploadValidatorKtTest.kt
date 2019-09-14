package lt.markmerkk.worklogs

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WorklogUploadValidatorKtTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun localLog() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now.plusMinutes(1),
                code = "WT-66",
                comment = "valid_comment",
                remoteData = null
        )

        val result = log.isEligibleForUpload()

        assertThat(result).isInstanceOf(WorklogValid::class.java)
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

        val result = log.isEligibleForUpload()

        assertThat(result).isInstanceOf(WorklogInvalidNoComment::class.java)
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

        val result = log.isEligibleForUpload()

        assertThat(result).isInstanceOf(WorklogInvalidNoTicketCode::class.java)
    }

    @Test
    fun durationLessThanOneMinute() {
        val now = timeProvider.now()
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                start = now,
                end = now.plusSeconds(59),
                code = "WT-66",
                comment = "valid_comment",
                remoteData = null
        )

        val result = log.isEligibleForUpload()

        assertThat(result).isInstanceOf(WorklogInvalidDurationTooLittle::class.java)
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

        val result = log.isEligibleForUpload()

        assertThat(result).isInstanceOf(WorklogInvalidAlreadyRemote::class.java)
    }
}