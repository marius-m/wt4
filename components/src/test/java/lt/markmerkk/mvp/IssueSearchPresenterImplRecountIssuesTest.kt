package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.interactors.IssueSearchInteractor
import org.junit.Assert.*
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-13
 */
class IssueSearchPresenterImplRecountIssuesTest {

    val view: IssueSearchMvp.View = mock()
    val interactor: IssueSearchInteractor = mock()
    val presenter = IssueSearchPresenterImpl(
            view,
            interactor,
            Schedulers.immediate(),
            Schedulers.immediate()
    )

    @Test
    fun valid_triggerIssueCount() {
        // Arrange
        whenever(interactor.issueCount()).thenReturn(Observable.just(5))

        // Act
        presenter.recountIssues()

        // Assert
        verify(view).showTotalIssueCount(any())
    }

    @Test
    fun errorGettinsIssue_triggerCountZero() {
        // Arrange
        whenever(interactor.issueCount())
                .thenReturn(Observable.error(IllegalStateException("error")))

        // Act
        presenter.recountIssues()

        // Assert
        verify(view).showTotalIssueCount(any())
    }
}