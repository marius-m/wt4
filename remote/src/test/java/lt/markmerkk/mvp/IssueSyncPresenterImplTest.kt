package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.LocalIssue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class IssueSyncPresenterImplTest {

    val view: IssueSyncMvp.View = mock()
    val clientProvider: JiraClientProvider = mock()
    val jiraInteractor: JiraInteractor = mock()
    val dataStorage: IDataStorage<LocalIssue> = mock()

    val presenter = IssueSyncPresenterImpl(
            view,
            clientProvider,
            jiraInteractor,
            dataStorage
    )

    @Before
    fun setUp() {
    }

    @Test
    fun valid_triggerDataChange() {
//        whenever(jiraInteractor.jiraIssues())
    }
}