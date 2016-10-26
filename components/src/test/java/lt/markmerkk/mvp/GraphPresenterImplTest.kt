package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer
import org.junit.Assert.*
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-10-26
 */
class GraphPresenterImplTest {
    val view: GraphMvp.View = mock()
    val logInteractor: GraphMvp.LogInteractor = mock()
    val presenter = GraphPresenterImpl(
            view = view,
            logInteractor = logInteractor,
            graphDrawers = listOf<GraphDrawer<Any>>(
                    mock()
            ),
            uiScheduler = Schedulers.immediate(),
            ioScheduler = Schedulers.immediate()
    )

    @Test
    fun validResponse_triggerProgress() {
        // Arrange
        whenever(logInteractor.loadLogs(any(), any()))
            .thenReturn(Observable.just(listOf(SimpleLog())))

        // Act
        presenter.loadGraph()

        // Assert
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    @Test
    fun validResponse_showGraph() {
        // Arrange
        whenever(logInteractor.loadLogs(any(), any()))
            .thenReturn(Observable.just(listOf(SimpleLog())))

        // Act
        presenter.loadGraph()

        // Assert
        verify(view).showGraph(any())
    }

    @Test
    fun errorResponse_showGraphError() {
        // Arrange
        whenever(logInteractor.loadLogs(any(), any()))
            .thenReturn(Observable.error(RuntimeException()))

        // Act
        presenter.loadGraph()

        // Assert
        verify(view).showErrorGraph(any())
    }

}