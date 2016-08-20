package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.interactors.VersionUpdater
import lt.markmerkk.interactors.VersioningInteractor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-15
 */
class VersioningMvpPresenterImplProgressTest {

    val view: VersioningMvp.View = mock()
    val versionUpdaterInteractor: VersionUpdater<Any> = mock()
    val versioningInteractor: VersioningInteractor<Any> = mock()
    val presenter = VersioningMvpPresenterImpl(
            view,
            versionUpdaterInteractor,
            versioningInteractor, 
            Schedulers.immediate(),
            Schedulers.immediate()
    )

    val progressSubject: BehaviorSubject<Float> = BehaviorSubject.create<Float>(0f)!!

    @Before
    fun setUp() {
        whenever(versionUpdaterInteractor.progressSubject)
                .thenReturn(progressSubject)
    }

    @Test
    fun onAttach_reportFirstProgress() {
        // Arrange
        // Act
        presenter.onAttach()

        // Assert
        verify(view).showProgress(eq(0f))
    }

    @Test
    fun reportProgress_reportAllProgress() {
        // Arrange
        // Act
        presenter.onAttach()
        progressSubject.onNext(0.1f)
        progressSubject.onNext(0.4f)
        progressSubject.onNext(0.8f)
        progressSubject.onNext(1.0f)


        // Assert
        verify(view, times(5)).showProgress(any())
    }
    
    @Test
    fun attachDetach_reportFirst() {
        // Arrange
        // Act
        presenter.onAttach()
        presenter.onDetach()
        progressSubject.onNext(0.1f)
        progressSubject.onNext(0.4f)
        progressSubject.onNext(0.8f)
        progressSubject.onNext(1.0f)


        // Assert
        verify(view, times(1)).showProgress(any())
    }
}