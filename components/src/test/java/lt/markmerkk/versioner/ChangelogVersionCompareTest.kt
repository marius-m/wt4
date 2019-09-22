package lt.markmerkk.versioner

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ChangelogVersionCompareTest {

    @Test
    fun equal() {
        // Assemble
        val v1 = Changelog.Version("", 1, 0, 0)
        val v2 = Changelog.Version("", 1, 0, 0)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isTrue()
        assertThat(resultBigger).isFalse()
    }

    @Test
    fun majorBigger() {
        // Assemble
        val v1 = Changelog.Version("", 2, 0, 0)
        val v2 = Changelog.Version("", 1, 0, 0)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isFalse()
        assertThat(resultBigger).isTrue()
    }

    @Test
    fun majorSmaller() {
        // Assemble
        val v1 = Changelog.Version("", 1, 0, 0)
        val v2 = Changelog.Version("", 2, 0, 0)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isFalse()
        assertThat(resultBigger).isFalse()
    }

    @Test
    fun minorBigger() {
        // Assemble
        val v1 = Changelog.Version("", 1, 1, 0)
        val v2 = Changelog.Version("", 1, 0, 0)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isFalse()
        assertThat(resultBigger).isTrue()
    }

    @Test
    fun minorSmaller() {
        // Assemble
        val v1 = Changelog.Version("", 1, 0, 0)
        val v2 = Changelog.Version("", 1, 1, 0)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isFalse()
        assertThat(resultBigger).isFalse()
    }

    @Test
    fun patchBigger() {
        // Assemble
        val v1 = Changelog.Version("", 1, 0, 1)
        val v2 = Changelog.Version("", 1, 0, 0)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isFalse()
        assertThat(resultBigger).isTrue()
    }

    @Test
    fun patchSmaller() {
        // Assemble
        val v1 = Changelog.Version("", 1, 0, 0)
        val v2 = Changelog.Version("", 1, 0, 1)

        // Act
        val resultEqual = v1 == v2
        val resultBigger = v1 > v2

        // Assert
        assertThat(resultEqual).isFalse()
        assertThat(resultBigger).isFalse()
    }
}