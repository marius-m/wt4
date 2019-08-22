package lt.markmerkk

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class WorklogStorageUpdateTest {

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
    fun updateLocalLog_noSuchLog() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(false).whenever(dbInteractor).existAsLocal(any())
        doReturn(false).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun updateLocalLog_existOnlyAsLocal() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(true).whenever(dbInteractor).existAsLocal(any())
        doReturn(false).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor).update(log)
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test // cannot be possible, can only update local entries
    fun updateLocalLog_existOnlyAsRemote() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1) // not possible, no remote ID
        doReturn(false).whenever(dbInteractor).existAsLocal(any())
        doReturn(true).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun updateLocalLog_existAsLocal_existAsRemote() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(true).whenever(dbInteractor).existAsLocal(any())
        doReturn(true).whenever(dbInteractor).existAsRemote(any()) // not possible, no remote ID

        // Act
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun updateRemoteLog_noSuchLog() {
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
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun updateRemoteLog_existOnlyAsLocal() {
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
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor).insert(
                Mocks.createLog(
                        timeProvider,
                        id = Const.NO_ID,
                        start = log.time.start,
                        end = log.time.end,
                        code = log.code.code,
                        comment = log.comment,
                        remoteData = null
                )
        )
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor).deleteByLocalId(eq(1))
        verify(dbInteractor).deleteByRemoteId(eq(2))
    }

    @Test
    fun updateRemoteLog_existOnlyAsRemote() {
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
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor).insert(
                Mocks.createLog(
                        timeProvider,
                        id = Const.NO_ID,
                        remoteData = null
                )
        )
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
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun updateRemoteLog_existAsBoth() {
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
        worklogStorage.updateSync(log)

        // Assert
        verify(dbInteractor).insert(
                Mocks.createLog(
                        timeProvider,
                        id = Const.NO_ID,
                        start = log.time.start,
                        end = log.time.end,
                        code = log.code.code,
                        comment = log.comment,
                        remoteData = null
                )
        )
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
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }
}