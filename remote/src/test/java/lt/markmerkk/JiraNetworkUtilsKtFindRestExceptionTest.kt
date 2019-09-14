package lt.markmerkk

import net.rcarz.jiraclient.RestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JiraNetworkUtilsKtFindRestExceptionTest {

    @Test
    fun noException() {
        val resultException: RestException? = RuntimeException()
                .findException()
        assertThat(resultException).isNull()
    }

    @Test
    fun valid() {
        val resultException: RestException? = RestException("message", 200, "", emptyArray())
                .findException()

        assertThat(resultException).isNotNull()
        assertThat(resultException).isInstanceOf(RestException::class.java)
    }

    @Test
    fun oneLevel() {
        val resultException: RestException? = RuntimeException(
                RestException("message", 200, "", emptyArray())
        ).findException()

        assertThat(resultException).isNotNull()
        assertThat(resultException).isInstanceOf(RestException::class.java)
    }

    @Test
    fun multiLevel() {
        val resultException: RestException? = RuntimeException(
                RuntimeException(
                        RestException("message", 200, "", emptyArray())
                )
        ).findException()

        assertThat(resultException).isNotNull()
        assertThat(resultException).isInstanceOf(RestException::class.java)
    }
}