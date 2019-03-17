package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.tickets.JiraSearchSubscriber
import net.rcarz.jiraclient.JiraClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class JiraInteractorImplLocalWorksTest {
    @Mock lateinit var clientProvider: JiraClientProvider
    @Mock lateinit var searchSubscriber: JiraSearchSubscriber
    @Mock lateinit var worklogSubscriber: JiraWorklogSubscriber
    @Mock lateinit var dataStorage: IDataStorage<SimpleLog>
    @Mock lateinit var issueStorage: IDataStorage<LocalIssue>
    @Mock lateinit var jiraClient: JiraClient

    lateinit var interactor: JiraInteractorImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = JiraInteractorImpl(
                clientProvider,
                dataStorage,
                issueStorage,
                searchSubscriber,
                worklogSubscriber,
                Schedulers.immediate()
        )
        val logs = listOf(
                SimpleLog(),
                SimpleLog(),
                SimpleLog()
        )
        doReturn(logs).whenever(dataStorage).data
        doReturn(Single.just(jiraClient)).whenever(clientProvider).clientStream()
    }

    @Test
    fun valid_emitListWithItems() {
        // Act
        val result = interactor.jiraLocalWorks()
                .test()

        // Assert
        result.assertNoErrors()
        result.assertValueCount(1) // list with items
    }

    @Test
    fun valid_listContainItems() {
        // Act
        val result = interactor.jiraLocalWorks()
                .test()

        // Assert
        assertEquals(3, result.onNextEvents[0].size)
    }

    @Test
    fun clientError_throwError() {
        // Assemble
        doReturn(Single.error<Any>(IllegalStateException())).whenever(clientProvider).clientStream()

        // Act
        val result = interactor.jiraLocalWorks()
                .test()

        // Assert
        result.assertError(IllegalStateException::class.java)
    }
}