package lt.markmerkk.versioner

import com.nhaarman.mockitokotlin2.argumentCaptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ChangelogTest {

    @Test
    fun valid() {
        // Assemble
        val input = "# Changelog\n" +
                "\n" +
                "Current: 1.5.7" +
                "\n" +
                "## 1.5.7\n" +
                "- Small bugfixes\n" +
                "\n"

        // Act
        val resultChangelog = Changelog.from(input)

        // Assert
        val version = resultChangelog.version
        assertThat(version.asString).isEqualTo("1.5.7")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(5)
        assertThat(version.patch).isEqualTo(7)
    }

    @Test
    fun noPatch() {
        // Assemble
        val input = "# Changelog\n" +
                "\n" +
                "Current: 1.5" + // no patch
                "\n" +
                "## 1.5.7\n" +
                "- Small bugfixes\n" +
                "\n"

        // Act
        val resultChangelog = Changelog.from(input)

        // Assert
        val version = resultChangelog.version
        assertThat(version.asString).isEqualTo("1.5")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(5)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun versionSingleDigit() {
        // Assemble
        val input = "# Changelog\n" +
                "\n" +
                "Current: 1" +
                "\n" +
                "## 1.5.7\n" +
                "- Small bugfixes\n" +
                "\n"

        // Act
        val resultChangelog = Changelog.from(input)

        // Assert
        val version = resultChangelog.version
        assertThat(version.asString).isEqualTo("1")
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun noCurrent() {
        // Assemble
        val input = "# Changelog\n" +
                "\n" +
                "## 1.5.7\n" +
                "- Small bugfixes\n" +
                "\n"

        // Act
        val resultChangelog = Changelog.from(input)

        // Assert
        val version = resultChangelog.version
        assertThat(version.asString).isEqualTo("")
        assertThat(version.major).isEqualTo(0)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    fun empty() {
        // Assemble
        val input = ""

        // Act
        val resultChangelog = Changelog.from(input)

        // Assert
        val version = resultChangelog.version
        assertThat(version.asString).isEqualTo("")
        assertThat(version.major).isEqualTo(0)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }
}