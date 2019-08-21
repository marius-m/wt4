package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single

class WorklogApiDeleteUnknownLogsTest {

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
                jiraClientProvider,
                jiraWorklogInteractor,
                ticketStorage,
                worklogStorage,
                userSettings
        )
    }

    @Test
    fun logsInSync() {
        // Assemble
        val now = timeProvider.now()
        val apiWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                )
        )
        val localWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                )
        )
        val apiWorlogsAsStream = Single.just(apiWorklogs)
        doReturn(Single.just(localWorklogs))
                .whenever(worklogStorage).loadWorklogs(any(), any())

        // Act
        val fetchResult = worklogApi.deleteUnknownLogs(
                apiWorklogsAsStream = apiWorlogsAsStream,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage, never()).hardDeleteRemote(any())
    }

    @Test
    fun tooManyWorklogsInLocalStorage() {
        // Assemble
        val now = timeProvider.now()
        val apiWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                ),
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 4L
                )
        )
        val localWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                ),
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 3L
                ),
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 4L
                )
        )
        val apiWorlogsAsStream = Single.just(apiWorklogs)
        doReturn(Single.just(localWorklogs))
                .whenever(worklogStorage).loadWorklogs(any(), any())

        // Act
        val fetchResult = worklogApi.deleteUnknownLogs(
                apiWorklogsAsStream = apiWorlogsAsStream,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage).hardDeleteRemoteSync(3L)
    }

    @Test
    fun tooLittleInLocalStorage() {
        // Assemble
        val now = timeProvider.now()
        val apiWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                ),
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 4L
                )
        )
        val localWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                )
        )
        val apiWorlogsAsStream = Single.just(apiWorklogs)
        doReturn(Single.just(localWorklogs))
                .whenever(worklogStorage).loadWorklogs(any(), any())

        // Act
        val fetchResult = worklogApi.deleteUnknownLogs(
                apiWorklogsAsStream = apiWorlogsAsStream,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage, never()).hardDeleteRemoteSync(any())
    }

    @Test
    fun onlyLocalLogs() {
        // Assemble
        val now = timeProvider.now()
        val apiWorklogs = listOf(
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 2L
                ),
                Mocks.createBasicLogRemote(
                        timeProvider,
                        remoteId = 4L
                )
        )
        val localWorklogs = listOf(
                Mocks.createBasicLog(
                        timeProvider,
                        localId = 2L
                ),
                Mocks.createBasicLog(
                        timeProvider,
                        localId = 3L
                ),
                Mocks.createBasicLog(
                        timeProvider,
                        localId = 4L
                )
        )
        val apiWorlogsAsStream = Single.just(apiWorklogs)
        doReturn(Single.just(localWorklogs))
                .whenever(worklogStorage).loadWorklogs(any(), any())

        // Act
        val fetchResult = worklogApi.deleteUnknownLogs(
                apiWorklogsAsStream = apiWorlogsAsStream,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage, never()).hardDeleteRemoteSync(any())
    }

}