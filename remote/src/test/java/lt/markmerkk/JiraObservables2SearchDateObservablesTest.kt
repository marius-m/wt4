package lt.markmerkk

import org.joda.time.DateTime
import org.junit.Assert.*
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class JiraObservables2SearchDateObservablesTest {
    val observableGen = JiraInteractorImpl(
            host = "fake_host",
            username = "fake_username",
            password = "fake_password"
    )

    @Test
    fun inputValid_shouldFormJql() {
        // Arrange
        val startDate = DateTime(10000)
        val endDate = DateTime(20000)

        // Act
        val output = observableGen.jqlForWorkIssuesFromDateObservable(startDate, endDate)

        // Assert
        assertThat(output).isNotNull()
        assertThat(output).isEqualTo("key in workedIssues(\"1970-01-01\", \"1970-01-01\", \"fake_username\")")
    }

}