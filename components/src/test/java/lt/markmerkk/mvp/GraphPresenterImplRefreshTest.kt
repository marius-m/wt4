package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import lt.markmerkk.interactors.GraphDrawer
import org.junit.Assert.*
import org.junit.Test
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-20
 */
class GraphPresenterImplRefreshTest {
    val view: GraphMvp.View = mock()
    val drawer: GraphDrawer<Any> = mock()
    val drawers = listOf(drawer)
    val logInteractor: GraphMvp.LogInteractor = mock()

    @Test
    fun valid_triggerRefresh() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.refresh()

        // Assert
        verify(drawer).refresh()
    }

    @Test
    fun emptyDrawers_noRefresh() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = emptyList(),
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.refresh()

        // Assert
        verify(drawer, never()).refresh()
    }

    @Test
    fun invalidSelectIndexOnDrawer_tooHigh_noRefresh() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )
        presenter.selectGraphIndex = 1000 // invalid index

        // Act
        presenter.refresh()

        // Assert
        verify(drawer, never()).refresh()
    }

    @Test
    fun invalidSelectIndexOnDrawer_sameAsSize_noRefresh() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )
        presenter.selectGraphIndex = 1 // invalid index

        // Act
        presenter.refresh()

        // Assert
        verify(drawer, never()).refresh()
    }

    @Test
    fun invalidSelectIndexOnDrawer_tooLow_noRefresh() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = drawers,
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )
        presenter.selectGraphIndex = -1 // invalid index

        // Act
        presenter.refresh()

        // Assert
        verify(drawer, never()).refresh()
    }
}