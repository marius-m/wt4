package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.exceptions.AuthException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import java.lang.RuntimeException

class WorklogApiUploadLogTest {

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
        doReturn(Single.just(outLog))
                .whenever(jiraWorklogInteractor).uploadWorklog(any(), any())

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertNoErrors()
        resultUpload.assertCompleted()
        resultUpload.assertValue(WorklogUploadSuccess(outLog))
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
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(jiraWorklogInteractor).uploadWorklog(any(), any())

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertNoErrors()
        resultUpload.assertCompleted()
        resultUpload.assertValueCount(1)
        val resultUploadValue = resultUpload.onNextEvents.first()
        assertThat(resultUploadValue).isInstanceOf(WorklogUploadError::class.java)
    }

    @Test
    fun validationError_noCode() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "", // no code should fail validation
                comment = "valid_comment",
                remoteData = null
        )

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertNoErrors()
        resultUpload.assertCompleted()
        resultUpload.assertValue(WorklogUploadValidationError(localLog, WorklogInvalidNoTicketCode(localLog)))
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
        doReturn(Single.error<Any>(JiraMocks.createAuthException()))
                .whenever(jiraWorklogInteractor).uploadWorklog(any(), any())

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertError(AuthException::class.java)
    }

}