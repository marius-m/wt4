package lt.markmerkk

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class WorklogStorageDeleteTest {

    @Mock
    lateinit var dbInteractor: DBInteractorLogJOOQ
    lateinit var worklogStorage: WorklogStorage

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        worklogStorage = WorklogStorage(
                timeProvider = timeProvider,
                dbInteractor = dbInteractor
        )
    }

    @Test
    fun noLogExist() {
        // Assemble
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        )
        doReturn(false).whenever(dbInteractor).existAsLocal(any())
        doReturn(false).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.deleteSync(log)

        // Assert
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun onlyLocalLog() {
        // Assemble
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        )
        doReturn(true).whenever(dbInteractor).existAsLocal(any())
        doReturn(false).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.deleteSync(log)

        // Assert
        verify(dbInteractor).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun onlyRemoteLog() {
        // Assemble
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        )
        doReturn(false).whenever(dbInteractor).existAsLocal(any())
        doReturn(true).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.deleteSync(log)

        // Assert
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
        verify(dbInteractor).update(
                Mocks.createLog(
                        timeProvider,
                        id = 1,
                        remoteData = Mocks.createRemoteData(
                                timeProvider,
                                remoteId = 2,
                                isDeleted = true
                        )
                )
        )
    }

    @Test
    fun existAsBoth() {
        // Assemble
        val log = Mocks.createLog(
                timeProvider,
                id = 1,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 2
                )
        )
        doReturn(true).whenever(dbInteractor).existAsLocal(any())
        doReturn(true).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.deleteSync(log)

        // Assert
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
        verify(dbInteractor).update(
                Mocks.createLog(
                        timeProvider,
                        id = 1,
                        remoteData = Mocks.createRemoteData(
                                timeProvider,
                                remoteId = 2,
                                isDeleted = true
                        )
                )
        )
    }
}