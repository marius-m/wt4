package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.entities.VersionSummary
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
class VersioningMvpPresenterImplUpdateStatusTest {

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

    val fakeSummary: VersionSummary<Any> = mock()
    val progressSubject: BehaviorSubject<Float> = BehaviorSubject.create<Float>(0f)!!

    @Before
    fun setUp() {
        whenever(versionUpdaterInteractor.progressSubject)
                .thenReturn(progressSubject)
    }

    @Test
    fun updateInProgress_triggerShowProgress() {
        // Arrange
        whenever(versioningInteractor.cacheUpdateSummary).thenReturn(null)
        whenever(versioningInteractor.loading).thenReturn(true)

        // Act
        presenter.checkUpdateSummary()

        // Assert
        verify(view, never()).showUpdateAvailable()
        verify(view, never()).showUpdateUnavailable()
        verify(view).showUpdateInProgress()
    }

    @Test
    fun summaryCached_updateInProgress_triggerShowProgress() {
        // Arrange
        whenever(versioningInteractor.cacheUpdateSummary).thenReturn(fakeSummary)
        whenever(versioningInteractor.loading).thenReturn(true)

        // Act
        presenter.checkUpdateSummary()

        // Assert
        verify(view, never()).showUpdateAvailable()
        verify(view, never()).showUpdateUnavailable()
        verify(view).showUpdateInProgress()
    }

    @Test
    fun noCache_notInProgress_triggerUnavailable() {
        // Arrange
        whenever(versioningInteractor.cacheUpdateSummary).thenReturn(null)
        whenever(versioningInteractor.loading).thenReturn(false)

        // Act
        presenter.checkUpdateSummary()

        // Assert
        verify(view, never()).showUpdateAvailable()
        verify(view).showUpdateUnavailable()
        verify(view, never()).showUpdateInProgress()
    }

    @Test
    fun cacheAvailable_notInProgress_triggerAvailable() {
        // Arrange
        whenever(versioningInteractor.cacheUpdateSummary).thenReturn(fakeSummary)
        whenever(versioningInteractor.loading).thenReturn(false)

        // Act
        presenter.checkUpdateSummary()

        // Assert
        verify(view).showUpdateAvailable()
        verify(view, never()).showUpdateUnavailable()
        verify(view, never()).showUpdateInProgress()
    }

    @Test
    fun fromLoadingToAvailable_triggerAvailable() {
        // Arrange
        whenever(versioningInteractor.cacheUpdateSummary)
                .thenReturn(null)
        whenever(versioningInteractor.loading)
                .thenReturn(true)

        // Act
        presenter.checkUpdateSummary()

        // Assert
        verify(view, never()).showUpdateAvailable()
        verify(view, never()).showUpdateUnavailable()
        verify(view).showUpdateInProgress()

        // -- Second iteration

        // Arrange
        reset(versioningInteractor)
        reset(view)
        whenever(versioningInteractor.cacheUpdateSummary)
                .thenReturn(fakeSummary)
        whenever(versioningInteractor.loading)
                .thenReturn(false)

        // Act
        presenter.checkUpdateSummary()

        // Assert
        verify(view).showUpdateAvailable()
        verify(view, never()).showUpdateUnavailable()
        verify(view, never()).showUpdateInProgress()
    }

    // test object

}