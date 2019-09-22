package lt.markmerkk.versioner

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Test

class ChangelogVersionTest {

    @Test
    fun valid() {
        // Assemble
        val input = "1.5.7"

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        assertThat(version.asString).isEqualTo("1.5.7")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(5)
        assertThat(version.patch).isEqualTo(7)
    }

    @Test
    fun noPatch() {
        // Assemble
        val input = "1.5"

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        assertThat(version.asString).isEqualTo("1.5")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(5)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun patchMalformed() {
        // Assemble
        val input = "1.5.asdf"

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        assertThat(version.asString).isEqualTo("1.5.asdf")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(5)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun noMinor() {
        // Assemble
        val input = "1"

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        Assertions.assertThat(version.asString).isEqualTo("1")
        Assertions.assertThat(version.major).isEqualTo(1)
        Assertions.assertThat(version.minor).isEqualTo(0)
        Assertions.assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun minorMalform() {
        // Assemble
        val input = "1.asdf"

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        assertThat(version.asString).isEqualTo("1.asdf")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun empty() {
        // Assemble
        val input = ""

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        assertThat(version.asString).isEqualTo("")
        assertThat(version.major).isEqualTo(0)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun malform() {
        // Assemble
        val input = "asdf"

        // Act
        val version = Changelog.versionFrom(input)

        // Assert
        assertThat(version.asString).isEqualTo("asdf")
        assertThat(version.major).isEqualTo(0)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

}