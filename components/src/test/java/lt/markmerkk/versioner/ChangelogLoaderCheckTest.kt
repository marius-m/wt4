package lt.markmerkk.versioner

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
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
    fun valid() {
        // Assemble
        doReturn(Single.just("valid_changelog"))
                .whenever(versionProvider).changelogAsString()

        // Act
        changelogLoader.check()

        // Assert
        verify(listener).onNewVersion(Changelog(
                version = Changelog.Version("", 0, 0, 0),
                contentAsString = "valid_changelog"
        ))
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