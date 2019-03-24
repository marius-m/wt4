package lt.markmerkk.tickets

import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.junit.Before
import org.junit.Test

class TicketLoaderIsTicketFreshEnoughTest {

    @Before
    fun setUp() {
        DateTimeUtils.setCurrentMillisFixed(0)
    }

    @Test
    fun freshEnough() {
        val resultExpired = TicketLoader.isTicketFreshEnough(
                lastTimeout = DateTime(0), // never
                timeoutInMinutes = 30, // expires in 30 minutes
                now = DateTime.now().plusMinutes(20)
        )

        assertThat(resultExpired).isTrue()
    }

    @Test
    fun tooOld() {
        val resultExpired = TicketLoader.isTicketFreshEnough(
                lastTimeout = DateTime(0), // never
                timeoutInMinutes = 30, // expires in 3 minutes
                now = DateTime.now().plusMinutes(40)
        )

        assertThat(resultExpired).isFalse()
    }

}