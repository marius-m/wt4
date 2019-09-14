package lt.markmerkk

import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.RestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JiraNetworkUtilsKtIsAuthExceptionTest {

    @Test
    fun noException() {
        val result = RuntimeException()
                .isAuthException()
        assertThat(result).isFalse()
    }

    @Test
    fun valid_success() {
        val result = RestException("message", 200, "", emptyArray())
                .isAuthException()

        assertThat(result).isFalse()
    }

    @Test
    fun oneLevel_success() {
        val result = RuntimeException(
                RestException("message", 200, "", emptyArray())
        ).isAuthException()

        assertThat(result).isFalse()
    }

    @Test
    fun restAuthException() {
        val result = RuntimeException(
                RestException("auth_error", 401, "", emptyArray())
        ).isAuthException()

        assertThat(result).isTrue()
    }

    @Test
    fun authException() {
        val result = AuthException(RuntimeException())
                .isAuthException()

        assertThat(result).isTrue()
    }

    @Test
    fun deeperAuthException() {
        val result = RuntimeException(
                AuthException(RuntimeException())
        ).isAuthException()

        assertThat(result).isTrue()
    }

    @Test
    fun otherError() {
        val result = RuntimeException(
                RestException("not_found", 404, "", emptyArray())
        ).isAuthException()

        assertThat(result).isFalse()
    }

}