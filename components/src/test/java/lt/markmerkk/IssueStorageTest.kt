package lt.markmerkk

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.IDataListener
import lt.markmerkk.IssueStorage
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.database.interfaces.IExecutor
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-11
 */
class IssueStorageTest {

    val executor: IExecutor = mock()
    val storage = IssueStorage(executor)

    @Test
    fun insert_triggerExecutor() {
        // Arrange
        // Act
        storage.insert(LocalIssue())

        // Assert
        verify(executor, times(1)).execute(any())
    }

    @Test
    fun delete_triggerExecutor() {
        // Arrange
        // Act
        storage.delete(LocalIssue())

        // Assert
        verify(executor, times(1)).execute(any())
    }

    @Test
    fun update_triggerExecutor() {
        // Arrange
        // Act
        storage.update(LocalIssue())

        // Assert
        verify(executor, times(1)).execute(any())
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