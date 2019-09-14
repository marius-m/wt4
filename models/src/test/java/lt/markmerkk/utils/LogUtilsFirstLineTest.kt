package lt.markmerkk.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogUtilsFirstLineTest {

    @Test
    fun noLines() {
        val result = LogUtils.firstLine("")

        assertThat(result).isEqualTo("")
    }

    @Test
    fun oneLine() {
        val result = LogUtils.firstLine("one_line")

        assertThat(result).isEqualTo("one_line")
    }

    @Test
    fun multipleLines() {
        val result = LogUtils.firstLine("one_line\ntwo_line")

        assertThat(result).isEqualTo("one_line")
    }
}