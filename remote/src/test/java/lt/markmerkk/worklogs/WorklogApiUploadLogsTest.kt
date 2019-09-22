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

class WorklogApiUploadLogsTest {

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
    fun validUpload() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = null
        )
        val outLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 1
                )
        )
        doReturn(Single.just(listOf(localLog)))
                .whenever(worklogStorage).loadWorklogs(any(), any())
        doReturn(Single.just(outLog))
                .whenever(jiraWorklogInteractor).uploadWorklog(any(), any())
        doReturn(Single.just(1))
                .whenever(worklogStorage).insertOrUpdate(any())

        // Act
        val fetchResult = worklogApi.uploadLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor).uploadWorklog(any(), eq(localLog))
        verify(worklogStorage).insertOrUpdate(eq(outLog))
    }

    @Test
    fun remoteLog() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = Mocks.createRemoteData(timeProvider, remoteId = 2)
        )
        doReturn(Single.just(listOf(localLog)))
                .whenever(worklogStorage).loadWorklogs(any(), any())

        // Act
        val fetchResult = worklogApi.uploadLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor, never()).uploadWorklog(any(), any())
        verify(worklogStorage, never()).insertOrUpdate(any())
    }

    @Test
    fun errorUploading() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = null
        )
        val outLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 1
                )
        )
        doReturn(Single.just(listOf(localLog)))
                .whenever(worklogStorage).loadWorklogs(any(), any())
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(jiraWorklogInteractor).uploadWorklog(any(), any())

        // Act
        val fetchResult = worklogApi.uploadLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(jiraWorklogInteractor).uploadWorklog(any(), eq(localLog))
        verify(worklogStorage, never()).insertOrUpdate(eq(outLog))
    }

    @Test
    fun authError() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = null
        )
        doReturn(Single.just(listOf(localLog)))
                .whenever(worklogStorage).loadWorklogs(any(), any())
        doReturn(Single.error<Any>(JiraMocks.createAuthException()))
                .whenever(jiraWorklogInteractor).uploadWorklog(any(), any())

        // Act
        val fetchResult = worklogApi.uploadLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertError(AuthException::class.java)
    }

}