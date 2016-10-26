package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.any
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
 * @since 2016-10-26
 */
class GraphPresenterImplHandleSuccessTest {
    val view: GraphMvp.View = mock()
    val logInteractor: GraphMvp.LogInteractor = mock()

    @Test
    fun noDrawers_showError() {
        // Arrange
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = emptyList<GraphDrawer<Any>>(),
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.handleSuccess(emptyList())

        // Assert
        verify(view).showErrorGraph(any())
    }

    @Test
    fun validGraphs_renderGraph() {
        // Arrange
        val drawer: GraphDrawer<Any> = mock()
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = listOf(drawer),
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )

        // Act
        presenter.handleSuccess(emptyList())

        // Assert
        verify(view).showGraph(drawer)
    }

    @Test
    fun invalidIndex_showError() {
        // Arrange
        val drawer: GraphDrawer<Any> = mock()
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = listOf(drawer, drawer, drawer),
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )
        presenter.selectGraphIndex = 100 // invalid index

        // Act
        presenter.handleSuccess(emptyList())

        // Assert
        verify(view, never()).showGraph(any())
        verify(view).showErrorGraph(any())
    }

    @Test
    fun invalidIndex2_showError() {
        // Arrange
        val drawer: GraphDrawer<Any> = mock()
        val presenter = GraphPresenterImpl(
                view = view,
                logInteractor = logInteractor,
                graphDrawers = listOf(drawer, drawer, drawer),
                uiScheduler = Schedulers.immediate(),
                ioScheduler = Schedulers.immediate()
        )
        presenter.selectGraphIndex = -1 // invalid index

        // Act
        presenter.handleSuccess(emptyList())

        // Assert
        verify(view, never()).showGraph(any())
        verify(view).showErrorGraph(any())
    }

}