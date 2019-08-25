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
import kotlin.math.log

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

        val localLogWithMessage = localLog.appendSystemNote("No ticket code")
        val streamValue: WorklogUploadValidationError = resultUpload.onNextEvents.first() as WorklogUploadValidationError
        assertThat(streamValue.worklogValidateError).isInstanceOf(WorklogInvalidNoTicketCode::class.java)
        assertThat(streamValue.localLog).isEqualTo(localLogWithMessage)
        verify(worklogStorage).updateSync(localLogWithMessage)
    }

    @Test
    fun validationError_noComment() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "", // no comment
                remoteData = null
        )

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertNoErrors()
        resultUpload.assertCompleted()

        val localLogWithMessage = localLog.appendSystemNote("No comment")
        val streamValue: WorklogUploadValidationError = resultUpload.onNextEvents.first() as WorklogUploadValidationError
        assertThat(streamValue.worklogValidateError).isInstanceOf(WorklogInvalidNoComment::class.java)
        assertThat(streamValue.localLog).isEqualTo(localLogWithMessage)
        verify(worklogStorage).updateSync(localLogWithMessage)
    }

    @Test
    fun validationError_durationTooLittle() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now, // duration too little
                code = "WT-4",
                comment = "valid_comment",
                remoteData = null
        )

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertNoErrors()
        resultUpload.assertCompleted()

        val localLogWithMessage = localLog.appendSystemNote("Duration must be at least 1 minute")
        val streamValue: WorklogUploadValidationError = resultUpload.onNextEvents.first() as WorklogUploadValidationError
        assertThat(streamValue.worklogValidateError).isInstanceOf(WorklogInvalidDurationTooLittle::class.java)
        assertThat(streamValue.localLog).isEqualTo(localLogWithMessage)
        verify(worklogStorage).updateSync(localLogWithMessage)
    }

    @Test
    fun validationError_worklogAlreadyRemote() {
        // Assemble
        val now = timeProvider.now()
        val localLog = Mocks.createLog(
                timeProvider,
                id = 1L,
                start = now,
                end = now.plusMinutes(5),
                code = "WT-4",
                comment = "valid_comment",
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        )

        // Act
        val resultUpload = worklogApi.uploadLog(now, localLog).test()

        // Assert
        resultUpload.assertNoErrors()
        resultUpload.assertCompleted()

        val streamValue: WorklogUploadValidationError = resultUpload.onNextEvents.first() as WorklogUploadValidationError
        assertThat(streamValue.worklogValidateError).isInstanceOf(WorklogInvalidAlreadyRemote::class.java)
        assertThat(streamValue.localLog).isEqualTo(localLog)
        verify(worklogStorage, never()).updateSync(any())
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