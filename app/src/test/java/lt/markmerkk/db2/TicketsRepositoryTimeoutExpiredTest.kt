package lt.markmerkk.db2

import lt.markmerkk.tickets.TicketsRepository
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.junit.Before
import org.junit.Test

class TicketsRepositoryTimeoutExpiredTest {

    @Before
    fun setUp() {
        DateTimeUtils.setCurrentMillisFixed(0)
    }

    @Test
    fun freshEnough() {
        val resultExpired = TicketsRepository.isFreshEnough(
                lastTimeout = DateTime(0),
                timeoutInDays = 4, // expires in 4 days
                now = DateTime.now().plusDays(3)
        )

        assertThat(resultExpired).isTrue()
    }

    @Test
    fun tooOld() {
        val resultExpired = TicketsRepository.isFreshEnough(
                lastTimeout = DateTime(0),
                timeoutInDays = 3, // expires in 3 days
                now = DateTime.now().plusDays(4)
        )

        assertThat(resultExpired).isFalse()
    }
}