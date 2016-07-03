package lt.markmerkk

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class JiraObservables2RemoteWorklogsTest {
    @Test
    fun crateTest() {
        val observableGenerator = JiraObservables2()

        assertNotNull(observableGenerator)
    }
}