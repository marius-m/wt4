package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer
import org.junit.Assert.*
import org.junit.Test
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-20
 */
class GraphPresenterImplRefreshButtonTest {
    val view: GraphMvp.View = mock()
    val drawer: GraphDrawer<Any> = mock()
    val drawers = listOf(drawer)
    val logInteractor: GraphMvp.LogInteractor = mock()

    @Test
    fun onInit_hideButton() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.onAttach()

        // Assert
        verify(view).hideRefreshButton()
    }

    @Test
    fun drawerHasButton_showButton() {
        // Arrange
        whenever(drawer.isRefreshable).thenReturn(true)
        val fakeLog: SimpleLog = mock()
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.handleSuccess(listOf(fakeLog))

        // Assert
        verify(view).showRefreshButton()
    }

    @Test
    fun noRefreshButton_hideButton() {
        // Arrange
        whenever(drawer.isRefreshable).thenReturn(false)
        val fakeLog: SimpleLog = mock()
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.handleSuccess(listOf(fakeLog))

        // Assert
        verify(view).hideRefreshButton()
    }
}