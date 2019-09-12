package lt.markmerkk.widgets.settings

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class AuthResultParserFindAccessTokenTest {

    private lateinit var parser: AuthResultParser
    private val validResponse = "You have successfully authorised 'OAuth Test'. Your verification code is 'HXuar0'. You will need to enter this exact text when prompted. You should write this value down before closing the browser window."

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        parser = AuthResultParser()
    }

    @Test
    fun valid() {
        // Act
        val resultToken = parser.findAccessToken(validResponse)

        // Assert
        assertThat(resultToken).isEqualTo("HXuar0")
    }

    @Test
    fun noContent() {
        // Act
        val resultToken = parser.findAccessToken("")

        // Assert
        assertThat(resultToken).isEqualTo("")
    }
}