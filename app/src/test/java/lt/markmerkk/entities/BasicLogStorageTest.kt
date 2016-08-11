package lt.markmerkk.entities

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.IDataListener
import lt.markmerkk.entities.database.interfaces.IExecutor
import org.junit.Ignore
import org.junit.Test

/**
 * Created by mariusmerkevicius on 12/13/15.
 */
class BasicLogStorageTest {

    val executor: IExecutor = mock()
    val storage = BasicLogStorage(executor)

    @Test
    fun insert_triggerExecutor() {
        // Arrange
        // Act
        storage.insert(SimpleLog())

        // Assert
        verify(storage.executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun delete_triggerExecutor() {
        // Arrange
        // Act
        storage.delete(SimpleLog())

        // Assert
        verify(storage.executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun update_triggerExecutor() {
        // Arrange
        // Act
        storage.update(SimpleLog())

        // Assert
        verify(storage.executor, times(2)).execute(any()) // insert/notify
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