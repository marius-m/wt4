package lt.markmerkk.utils

import lt.markmerkk.Const
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UriUtilsParseUriTest {

    @Test fun testValid() {
        // Arrange

        // Act
        val resultId = UriUtils.parseUri("https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051")

        // Assert
        assertThat(resultId).isEqualTo(73051)
    }

    @Test fun testInputNull() {
        // Arrange

        // Act
        val resultId = UriUtils.parseUri(null)

        // Assert
        assertThat(resultId).isEqualTo(Const.NO_ID)
    }

    @Test fun testInputEmpty() {
        // Arrange
        // Act
        val resultId = UriUtils.parseUri("")

        // Assert
        assertThat(resultId).isEqualTo(Const.NO_ID)
    }

    @Test fun testInputMalformed() {
        // Arrange

        // Act
        val resultId = UriUtils.parseUri("asdf")

        // Assert
        assertThat(resultId).isEqualTo(Const.NO_ID)
    }

}