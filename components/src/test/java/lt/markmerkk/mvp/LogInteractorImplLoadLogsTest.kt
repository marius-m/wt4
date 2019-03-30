package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.entities.SimpleLog
import org.junit.Assert.*
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-10-25
 */
class LogInteractorImplLoadLogsTest {
    val resultProvider: QueryResultProvider<List<SimpleLog>> = mock()
    val interactor = LogInteractorImpl(
            resultProvider,
            Schedulers.immediate()
    )

    @Test
    fun test_observableCreate() {
        // Arrange
        // Act
        val result = interactor.loadLogs(
                1000,
                2000
        )

        // Assert
        assertNotNull(result)
    }

    @Test
    fun correctResult() {
        // Arrange
        val response = listOf(SimpleLog())
        whenever(resultProvider.resultFrom(any()))
                .thenReturn(response)
        val testSubscriber = TestSubscriber<List<SimpleLog>>()

        // Act
        val observable = interactor.loadLogs(1000, 2000)
        observable.subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1)
        val result = testSubscriber.onNextEvents.get(0)
        assertEquals(response, result)
    }

    @Test
    fun nullResponse() {
        // Arrange
        val response = null
        whenever(resultProvider.resultFrom(any()))
                .thenReturn(response)
        val testSubscriber = TestSubscriber<List<SimpleLog>>()

        // Act
        val observable = interactor.loadLogs(1000, 2000)
        observable.subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1)
        val result = testSubscriber.onNextEvents.get(0)
        assertNotNull(result)
    }

}