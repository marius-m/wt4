package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.exceptions.AuthException
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import java.lang.RuntimeException

class WorklogApiDeleteMarkedLogsTest {

    @Mock lateinit var jiraClientProvider: JiraClientProvider
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
                timeProvider,
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
        doReturn(2L).whenever(worklogStorage).hardDeleteRemoteSync(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor).delete(any())
        verify(worklogStorage).hardDeleteRemoteSync(remoteId)
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
        verify(worklogStorage, never()).hardDeleteRemoteSync(eq(remoteId))
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
        doReturn(2L).whenever(worklogStorage).hardDeleteRemoteSync(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor, never()).delete(any())
        verify(worklogStorage, never()).hardDeleteRemoteSync(eq(remoteId))
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
        doReturn(2L).whenever(worklogStorage).hardDeleteRemoteSync(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor).delete(any())
        verify(worklogStorage).hardDeleteRemoteSync(eq(remoteId))
    }

    @Test
    fun authError() {
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
        doReturn(Single.error<Any>(JiraMocks.createAuthException()))
                .whenever(jiraWorklogInteractor).delete(any())
        doReturn(2L).whenever(worklogStorage).hardDeleteRemoteSync(any())

        // Act
        val fetchResult = worklogApi.deleteMarkedLogs(
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertError(AuthException::class.java)
        verify(jiraClientProvider).markAsError()
        verify(worklogStorage, never()).hardDeleteRemoteSync(any())
    }

}