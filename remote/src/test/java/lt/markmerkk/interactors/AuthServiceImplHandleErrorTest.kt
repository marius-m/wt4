package lt.markmerkk.interactors

import net.rcarz.jiraclient.RestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class AuthServiceImplHandleErrorTest {

    @Test
    fun illegalParamsPassed() {
        val resultError = AuthServiceImpl.handleError(IllegalArgumentException("Invalid params"))

        assertThat(resultError).isEqualTo(AuthService.AuthResult.ERROR_EMPTY_FIELDS)
    }

    @Test
    fun unauthorized() {
        val restException = RestException(
                "message",
                401,
                "result_message",
                emptyArray()
        )
        val resultError = AuthServiceImpl.handleError(
                RuntimeException(
                        restException
                )
        )

        assertThat(resultError).isEqualTo(AuthService.AuthResult.ERROR_UNAUTHORISED)
    }

    @Test
    fun hostnameFailure() {
        val restException = RestException(
                "message",
                404,
                "hostname_not_found",
                emptyArray()
        )
        val resultError = AuthServiceImpl.handleError(
                RuntimeException(
                        restException
                )
        )

        assertThat(resultError).isEqualTo(AuthService.AuthResult.ERROR_INVALID_HOSTNAME)
    }

    @Test
    fun somethingElseCode() {
        val restException = RestException(
                "message",
                500, // malformed url
                "hostname_not_found",
                emptyArray()
        )
        val resultError = AuthServiceImpl.handleError(
                RuntimeException(
                        restException
                )
        )

        assertThat(resultError).isEqualTo(AuthService.AuthResult.ERROR_UNDEFINED)
    }

    @Test
    fun noRestException() {
        val resultError = AuthServiceImpl.handleError(
                RuntimeException("random exception")
        )

        assertThat(resultError).isEqualTo(AuthService.AuthResult.ERROR_UNDEFINED)
    }
}