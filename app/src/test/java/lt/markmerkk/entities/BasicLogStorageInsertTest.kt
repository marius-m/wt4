package lt.markmerkk.entities

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import lt.markmerkk.entities.database.interfaces.IExecutor
import org.junit.Test

/**
 * Created by mariusmerkevicius on 12/13/15.
 */
class BasicLogStorageInsertTest {

    val executor: IExecutor = mock()
    val storage = FakeBasicLogStorage(executor)

    @Test
    fun testValid() {
        // Arrange
        // Act
        storage.insert(SimpleLog())

        // Assert
        verify(storage.executor).execute(any())
    }

    @Test
    fun testNullJob() {
        // Arrange
        // Act
        storage.insert(null)

        // Assert
        verify(storage.executor, never()).execute(any())
    }

    // Test classes

    class FakeBasicLogStorage(executor: IExecutor) : BasicLogStorage(executor) {
        override fun notifyDataChange() {
            // No notification
        }
    }

}