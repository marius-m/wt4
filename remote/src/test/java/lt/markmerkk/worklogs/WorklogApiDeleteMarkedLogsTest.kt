package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single
import java.lang.RuntimeException

class WorklogApiDeleteMarkedLogsTest {

    @Mock lateinit var jiraClientProvider: JiraClientProvider2
    @Mock lateinit var jiraWorklogInteractor: JiraWorklogInteractor
    @Mock lateinit var ticketStorage: TicketStorage
    @Mock lateinit var worklogStorage: WorklogStorage
    @Mock lateinit var userSettings: UserSettings
    lateinit var worklogApi: WorklogApi

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        worklogApi = WorklogApi(
                jiraClientProvider,
                jiraWorklogInteractor,
                ticketStorage,
                worklogStorage
        )
    }

    @Test
    fun validDelete() {
        // Assemble
        val remoteId: Long = 2
        val now = timeProvider.now()
        val worklog1 = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2,
                        isDeleted = true
                )
        )
        doReturn(Single.just(listOf(worklog1)))
                .whenever(worklogStorage).loadWorklogs(any(), any())
        doReturn(Single.just(remoteId))
                .whenever(jiraWorklogInteractor).delete(any())
        doReturn(Single.just(2))
                .whenever(worklogStorage).hardDeleteRemote(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor).delete(any())
        verify(worklogStorage).hardDeleteRemote(eq(remoteId))
    }

    @Test
    fun localLog() {
        // Assemble
        val remoteId: Long = 2
        val now = timeProvider.now()
        val worklog1 = Mocks.createLog(
                timeProvider,
                id = 1
        )
        doReturn(Single.just(listOf(worklog1)))
                .whenever(worklogStorage).loadWorklogs(any(), any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor, never()).delete(any())
        verify(worklogStorage, never()).hardDeleteRemote(eq(remoteId))
    }

    @Test
    fun notMarkedForDelete() {
        // Assemble
        val remoteId: Long = 2
        val now = timeProvider.now()
        val worklog1 = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2,
                        isDeleted = false
                )
        )
        doReturn(Single.just(listOf(worklog1)))
                .whenever(worklogStorage).loadWorklogs(any(), any())
        doReturn(Single.just(remoteId))
                .whenever(jiraWorklogInteractor).delete(any())
        doReturn(Single.just(2))
                .whenever(worklogStorage).hardDeleteRemote(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor, never()).delete(any())
        verify(worklogStorage, never()).hardDeleteRemote(eq(remoteId))
    }

    @Test
    fun errorDeleting() {
        // Assemble
        val remoteId: Long = 2
        val now = timeProvider.now()
        val worklog1 = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2,
                        isDeleted = true
                )
        )
        doReturn(Single.just(listOf(worklog1)))
                .whenever(worklogStorage).loadWorklogs(any(), any())
        doReturn(Single.error<Any>(RuntimeException("Error trying to delete worklog")))
                .whenever(jiraWorklogInteractor).delete(any())
        doReturn(Single.just(2))
                .whenever(worklogStorage).hardDeleteRemote(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor).delete(any())
        verify(worklogStorage).hardDeleteRemote(eq(remoteId))
    }

}