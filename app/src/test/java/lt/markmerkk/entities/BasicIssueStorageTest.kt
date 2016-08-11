package lt.markmerkk.entities

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-11
 */
class BasicIssueStorageTest {

    val executor: IExecutor = mock()
    val storage = BasicIssueStorage(executor)

    @Test
    fun insert_triggerExecutor() {
        // Arrange
        // Act
        storage.insert(LocalIssue())

        // Assert
        verify(executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun delete_triggerExecutor() {
        // Arrange
        // Act
        storage.delete(LocalIssue())

        // Assert
        verify(executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun update_triggerExecutor() {
        // Arrange
        // Act
        storage.update(LocalIssue())

        // Assert
        verify(executor, times(2)).execute(any()) // insert/notify
    }

    @Test
    fun register_triggerListener() {
        // Arrange
        val dataListener: IDataListener<LocalIssue> = mock()

        // Act
        storage.register(dataListener)
        storage.update(LocalIssue())
        storage.unregister(dataListener)

        // Assert
        verify(dataListener).onDataChange(any())
    }

    @Test
    fun registerUnregister_noListenerTrigger() {
        // Arrange
        val dataListener: IDataListener<LocalIssue> = mock()

        // Act
        storage.register(dataListener)
        storage.unregister(dataListener)
        storage.update(LocalIssue())

        // Assert
        verify(dataListener, never()).onDataChange(any())
    }
}