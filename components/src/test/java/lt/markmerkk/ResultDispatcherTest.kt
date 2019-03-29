package lt.markmerkk

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ResultDispatcherTest {

    @Test
    fun valid() {
        // Assemble
        val key = "valid_key"
        val value = "valid_value"
        val resultDispatcher = ResultDispatcher()
                .apply { publish(key, value) }

        // Act
        val resultItem = resultDispatcher.consume(key, String::class.java)

        // Assert
        assertThat(resultItem).isEqualTo(value)
    }

    @Test
    fun noItem() {
        // Assemble
        val key = "valid_key"
        val resultDispatcher = ResultDispatcher()

        // Act
        val resultItem = resultDispatcher.consume(key, String::class.java)

        // Assert
        assertThat(resultItem).isNull()
    }

    @Test
    fun noKey() {
        // Assemble
        val key = "valid_key"
        val value = "valid_value"
        val resultDispatcher = ResultDispatcher()
                .apply { publish(key, value) }

        // Act
        val resultItem = resultDispatcher.consume("invalid_key", String::class.java)

        // Assert
        assertThat(resultItem).isNull()
    }

    @Test
    fun invalidResultType() {
        // Assemble
        val key = "valid_key"
        val value = 1
        val resultDispatcher = ResultDispatcher()
                .apply { publish(key, value) }

        // Act
        val resultItem = resultDispatcher.consume(key, String::class.java) // incorrect value type asked

        // Assert
        assertThat(resultItem).isNull()
    }
}