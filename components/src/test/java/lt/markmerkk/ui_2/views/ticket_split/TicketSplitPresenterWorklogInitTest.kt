package lt.markmerkk.ui_2.views.ticket_split

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.utils.LogSplitter
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TicketSplitPresenterWorklogInitTest {
    @Mock lateinit var view: TicketSplitContract.View
    @Mock lateinit var inputLog: SimpleLog
    @Mock lateinit var logStorage: LogStorage
    @Mock lateinit var strings: Strings
    @Mock lateinit var ticketStorage: TicketStorage
    lateinit var presenter: TicketSplitPresenter
    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = TicketSplitPresenter(
                inputLog,
                timeProvider,
                logStorage,
                LogSplitter,
                strings,
                ticketStorage,
                SchedulerProviderImmediate()
        )
        doReturn("valid_string").whenever(strings).getString(any())
        presenter.onAttach(view)
    }

    @Test
    fun valid() {
        // Act
        presenter.handleWorklogInit(Mocks.createLocalLog(timeProvider))

        // Assert
        verify(view).hideError()
        verify(view).onWorklogInit(
                showTicket = true,
                ticketCode = "DEV-123",
                originalComment = "valid_comment",
                isSplitEnabled = true
        )
    }

    @Test
    fun noTicket() {
        // Act
        presenter.handleWorklogInit(
                Mocks.createLocalLog(
                        timeProvider,
                        task = ""
                )
        )

        // Assert
        verify(view).hideError()
        verify(view).onWorklogInit(
                showTicket = false,
                ticketCode = "",
                originalComment = "valid_comment",
                isSplitEnabled = true
        )
    }

    @Test
    fun remoteTask() {
        // Act
        presenter.handleWorklogInit(Mocks.mockRemoteLog(timeProvider, task = "DEV-123"))

        // Assert
        verify(view).onWorklogInit(
                showTicket = true,
                ticketCode = "DEV-123",
                originalComment = "valid_comment",
                isSplitEnabled = false
        )
        verify(view).showError(any())
    }
}