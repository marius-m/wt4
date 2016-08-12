package lt.markmerkk

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.database.interfaces.IExecutor
import org.junit.Ignore
import org.junit.Test

/**
 * Created by mariusmerkevicius on 12/13/15.
 */
class LogStorageTest {

    val executor: IExecutor = mock()
    val storage = LogStorage(executor)

    @Test
    fun insert_triggerExecutor() {
        // Arrange
        reset(executor)

        // Act
        storage.insert(SimpleLog())

        // Assert
        verify(executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun delete_triggerExecutor() {
        // Arrange
        reset(executor)

        // Act
        storage.delete(SimpleLog())

        // Assert
        verify(executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun update_triggerExecutor() {
        // Arrange
        reset(executor)

        // Act
        storage.update(SimpleLog())

        // Assert
        verify(executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun register_triggerListener() {
        // Arrange
        val dataListener: IDataListener<SimpleLog> = mock()

        // Act
        storage.register(dataListener)
        storage.update(SimpleLog())
        storage.unregister(dataListener)

        // Assert
        verify(dataListener).onDataChange(any())
    }

    @Test
    fun registerUnregister_noListenerTrigger() {
        // Arrange
        val dataListener: IDataListener<SimpleLog> = mock()

        // Act
        storage.register(dataListener)
        storage.unregister(dataListener)
        storage.update(SimpleLog())

        // Assert
        verify(dataListener, never()).onDataChange(any())
    }

}