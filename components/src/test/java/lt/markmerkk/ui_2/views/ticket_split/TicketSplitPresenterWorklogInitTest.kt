package lt.markmerkk.ui_2.views.ticket_split

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Mocks
import lt.markmerkk.SchedulerProviderImmediate
import lt.markmerkk.Strings
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.utils.LogSplitter
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single

class TicketSplitPresenterWorklogInitTest {
    @Mock lateinit var view: TicketSplitContract.View
    @Mock lateinit var strings: Strings
    @Mock lateinit var ticketStorage: TicketStorage
    @Mock lateinit var activeDisplayRepository: ActiveDisplayRepository
    lateinit var presenter: TicketSplitPresenter
    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val inputLog = Mocks.createLog(timeProvider)
        presenter = TicketSplitPresenter(
            inputLog,
            timeProvider,
            LogSplitter,
            strings,
            ticketStorage,
            SchedulerProviderImmediate(),
            activeDisplayRepository
        )
        doReturn("valid_string").whenever(strings).getString(any())
        doReturn(Single.just(Mocks.createTicket())).whenever(ticketStorage).findTicketsByCode(any())
        presenter.onAttach(view)
        reset(view)
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        presenter.handleWorklogInit(Mocks.createLog(timeProvider))

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
                Mocks.createLog(
                        timeProvider,
                        code = ""
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
        presenter.handleWorklogInit(Mocks.createBasicLogRemote(timeProvider, code = "DEV-123"))

        // Assert
        verify(view).onWorklogInit(
                showTicket = true,
                ticketCode = "DEV-123",
                originalComment = "valid_comment",
                isSplitEnabled = false
        )
        verify(view).hideError()
    }
}