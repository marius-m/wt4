package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.interactors.IssueSearchInteractor
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-12
 */
class IssueSearchPresenterImplTest {

    val view: IssueSearchMvp.View = mock()
    val interactor: IssueSearchInteractor = mock()
    val testScheduler = Schedulers.test()
    val presenter = IssueSearchPresenterImpl(
            view,
            interactor,
            testScheduler,
            testScheduler
    )

    @Before
    fun setUp() {
        presenter.onAttach()
    }

    @Test
    fun validThousandResult_showOnly100() {
        // Arrange
        val resultList = mutableListOf<LocalIssue>()
        for (i in 1..1000) {
            resultList.add(LocalIssue())
        }
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("test_phrase")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(view).showIssues(capture {
            assertEquals(100, it.size)
        })
    }

    @Test
    fun valid_triggerResult() {
        // Arrange
        val resultList = listOf<LocalIssue>(LocalIssue(), LocalIssue(), LocalIssue())
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("test_phrase")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(view).showIssues(any())
   }

    @Test
    fun fillQuery_runFullQuery() {
        // Arrange
        val resultList = listOf<LocalIssue>(LocalIssue(), LocalIssue(), LocalIssue())
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("test_phrase")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(interactor).searchIssues(eq("test_phrase"))
        verify(interactor, never()).allIssues()
    }

    @Test
    fun emptyQuery_returnAll() {
        // Arrange
        val resultList = listOf<LocalIssue>(LocalIssue(), LocalIssue(), LocalIssue())
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(interactor, never()).searchIssues(any())
        verify(interactor).allIssues()
    }

    @Test
    fun lowSymbolCount_returnAll() {
        // Arrange
        val resultList = listOf<LocalIssue>(LocalIssue(), LocalIssue(), LocalIssue())
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("aa")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(interactor, never()).searchIssues(any())
        verify(interactor).allIssues()
    }

    @Test
    fun spamSearch_triggerOnce() {
        // Arrange
        val resultList = listOf<LocalIssue>(LocalIssue(), LocalIssue(), LocalIssue())
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("test_phrase")
        presenter.search("test_phrase")
        presenter.search("test_phrase")
        presenter.search("test_phrase")
        presenter.search("test_phrase")
        presenter.search("test_phrase")
        presenter.search("test_phrase")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(interactor, times(1)).searchIssues(any())
    }

    @Test
    fun noResult_triggerHide() {
        // Arrange
        val resultList = emptyList<LocalIssue>()
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.just(resultList))

        // Act
        presenter.search("test_phrase")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(view).hideIssues()
    }

    @Test
    fun error_triggerHide() {
        // Arrange
        whenever(interactor.searchIssues(any()))
                .thenReturn(Observable.error(IllegalStateException("error")))

        // Act
        presenter.search("test_phrase")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(view).hideIssues()
    }
}