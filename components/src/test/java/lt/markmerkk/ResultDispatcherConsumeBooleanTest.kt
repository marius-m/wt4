package lt.markmerkk

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ResultDispatcherConsumeBooleanTest {

    @Test
    fun valid() {
        // Assemble
        val key = "valid_key"
        val value = true
        val resultDispatcher = ResultDispatcher()
                .apply { publish(key, value) }

        // Act
        val resultItem = resultDispatcher.consumeBoolean(key = key, defaultValue = false)

        // Assert
        assertThat(resultItem).isEqualTo(value)
    }

    @Test
    fun noItem() {
        // Assemble
        val key = "valid_key"
        val resultDispatcher = ResultDispatcher()

        // Act
        val resultItem = resultDispatcher.consumeBoolean(key = key, defaultValue = false)

        // Assert
        assertThat(resultItem).isFalse()
    }

    @Test
    fun noItem_diffDefaultValue() {
        // Assemble
        val key = "valid_key"
        val resultDispatcher = ResultDispatcher()

        // Act
        val resultItem = resultDispatcher.consumeBoolean(key = key, defaultValue = true)

        // Assert
        assertThat(resultItem).isTrue()
    }

    @Test
    fun noKey() {
        // Assemble
        val key = "valid_key"
        val value = "valid_value"
        val resultDispatcher = ResultDispatcher()
                .apply { publish(key, value) }

        // Act
        val resultItem = resultDispatcher.consumeBoolean(key = "invalid_key", defaultValue = false)

        // Assert
        assertThat(resultItem).isEqualTo(false)
    }

    @Test
    fun invalidResultType() {
        // Assemble
        val key = "valid_key"
        val value = 1
        val resultDispatcher = ResultDispatcher()
                .apply { publish(key, value) }

        // Act
        val resultItem = resultDispatcher.consumeBoolean(key = key, defaultValue = false) // incorrect value type asked

        // Assert
        assertThat(resultItem).isFalse()
    }
}