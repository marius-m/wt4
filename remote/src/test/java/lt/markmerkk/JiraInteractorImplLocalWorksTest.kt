package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.IDataStorage
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class JiraInteractorImplLocalWorksTest {
    val testSubscriber = TestSubscriber<List<SimpleLog>>()
    val clientProvider: JiraClientProvider = mock()
    val searchSubscriber: JiraSearchSubscriber = mock()
    val worklogSubscriber: JiraWorklogSubscriber = mock()
    val dataStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()
    val interactor = JiraInteractorImpl(
            clientProvider,
            dataStorage,
            issueStorage,
            searchSubscriber,
            worklogSubscriber,
            Schedulers.immediate()
    )

    @Before
    fun setUp() {
        doReturn(
                listOf(
                        SimpleLog(),
                        SimpleLog(),
                        SimpleLog()
                )
        ).whenever(dataStorage).data
    }

    @Test
    fun valid_emitListWithItems() {
        // Act
        interactor.jiraLocalWorks()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)


        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1) // list with items
    }

    @Test
    fun valid_listContainItems() {
        // Act
        interactor.jiraLocalWorks()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)


        // Assert
        assertEquals(3, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun clientError_throwError() {
        // Assemble
        whenever(clientProvider.client()).thenThrow(IllegalStateException("error_getting_client"))

        // Act
        interactor.jiraLocalWorks()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertError(IllegalStateException::class.java)
    }
}