package lt.markmerkk.versioner

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class ChangelogLoaderCheckTest {

    @Mock lateinit var listener: ChangelogLoader.Listener
    @Mock lateinit var versionProvider: VersionProvider
    lateinit var changelogLoader: ChangelogLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        changelogLoader = ChangelogLoader(
                listener,
                versionProvider,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun valid_versionAvailable() {
        // Assemble
        doReturn(Single.just("valid_changelog\nCurrent: 1.5.7"))
                .whenever(versionProvider).changelogAsString()
        doReturn(Changelog.versionFrom("1.5.6"))
                .whenever(versionProvider).currentVersion()

        // Act
        changelogLoader.check()

        // Assert
        verify(listener).onNewVersion(any())
    }

    @Test
    fun newestVersion() {
        // Assemble
        doReturn(Single.just("valid_changelog\nCurrent: 1.5.6"))
                .whenever(versionProvider).changelogAsString()
        doReturn(Changelog.versionFrom("1.5.6"))
                .whenever(versionProvider).currentVersion()

        // Act
        changelogLoader.check()

        // Assert
        verify(listener, never()).onNewVersion(any())
    }

    @Test
    fun wayLowerVersion() {
        // Assemble
        doReturn(Single.just("valid_changelog\nCurrent: 1.5.7"))
                .whenever(versionProvider).changelogAsString()
        doReturn(Changelog.versionFrom("1.0.0"))
                .whenever(versionProvider).currentVersion()

        // Act
        changelogLoader.check()

        // Assert
        verify(listener).onNewVersion(any())
    }

    @Test
    fun error() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(versionProvider).changelogAsString()

        // Act
        changelogLoader.check()

        // Assert
        verifyZeroInteractions(listener)
    }
}