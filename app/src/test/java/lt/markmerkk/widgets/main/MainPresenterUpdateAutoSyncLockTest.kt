package lt.markmerkk.widgets.main

import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MainPresenterUpdateAutoSyncLockTest {

    @Mock lateinit var view: MainContract.View
    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter()
        presenter.onAttach(view)
    }

    @Test
    fun unlocked() {
        // Act
        presenter.updateAutoSyncLock(
                isOpenLogDetails = false,
                isOpeningLogDetails = false,
                isOpenTickets = false,
                isOpeningTickets = false
        )

        // Assert
        verify(view).onAutoSyncLockChange(false)
    }

    @Test
    fun logOpen() {
        // Act
        presenter.updateAutoSyncLock(
                isOpenLogDetails = true,
                isOpeningLogDetails = false,
                isOpenTickets = false,
                isOpeningTickets = false
        )

        // Assert
        verify(view).onAutoSyncLockChange(true)
    }

    @Test
    fun logOpening() {
        // Act
        presenter.updateAutoSyncLock(
                isOpenLogDetails = false,
                isOpeningLogDetails = true,
                isOpenTickets = false,
                isOpeningTickets = false
        )

        // Assert
        verify(view).onAutoSyncLockChange(true)
    }

    @Test
    fun ticketsOpen() {
        // Act
        presenter.updateAutoSyncLock(
                isOpenLogDetails = false,
                isOpeningLogDetails = false,
                isOpenTickets = true,
                isOpeningTickets = false
        )

        // Assert
        verify(view).onAutoSyncLockChange(true)
    }

    @Test
    fun ticketsOpening() {
        // Act
        presenter.updateAutoSyncLock(
                isOpenLogDetails = false,
                isOpeningLogDetails = false,
                isOpenTickets = false,
                isOpeningTickets = true
        )

        // Assert
        verify(view).onAutoSyncLockChange(true)
    }

    @Test
    fun bothOpen() {
        // Act
        presenter.updateAutoSyncLock(
                isOpenLogDetails = true,
                isOpeningLogDetails = false,
                isOpenTickets = true,
                isOpeningTickets = false
        )

        // Assert
        verify(view).onAutoSyncLockChange(true)
    }
}