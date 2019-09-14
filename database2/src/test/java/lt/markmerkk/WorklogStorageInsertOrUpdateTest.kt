package lt.markmerkk

import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class WorklogStorageInsertOrUpdateTest {

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
    fun insertLocalLog_noSuchLog() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(false).whenever(dbInteractor).existAsLocal(any())
        doReturn(false).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(log)
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun insertLocalLog_existOnlyAsLocal() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(true).whenever(dbInteractor).existAsLocal(any())
        doReturn(false).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor).update(log)
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test // should not be possible
    fun insertLocalLog_existOnlyAsRemote() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(false).whenever(dbInteractor).existAsLocal(any())
        doReturn(true).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(log)
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test // should not be possible, as only local log being inserted
    fun insertLocalLog_existLocal_existRemote() {
        // Assemble
        val log = Mocks.createLog(
                timeProvider,
                id = 1
        )
        doReturn(true).whenever(dbInteractor).existAsLocal(any())
        doReturn(true).whenever(dbInteractor).existAsRemote(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor).update(log)
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun insertRemote_noSuchLog() {
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
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(log)
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).deleteByLocalId(any())
        verify(dbInteractor, never()).deleteByRemoteId(any())
    }

    @Test
    fun insertRemote_onlyLocalExists() {
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
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(
                Mocks.createLog(
                        timeProvider,
                        id = Const.NO_ID,
                        remoteData = Mocks.createRemoteData(
                                timeProvider,
                                remoteId = 2
                        )
                )
        )
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor).deleteByLocalId(eq(1))
        verify(dbInteractor).deleteByRemoteId(eq(2))
    }

    @Test
    fun insertRemote_onlyRemoteExists() {
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
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(
                Mocks.createLog(
                        timeProvider,
                        id = Const.NO_ID,
                        remoteData = Mocks.createRemoteData(
                                timeProvider,
                                remoteId = 2
                        )
                )
        )
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor).deleteByLocalId(eq(1))
        verify(dbInteractor).deleteByRemoteId(eq(2))
    }

    @Test
    fun insertRemote_existLocally_existRemotely() {
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
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(
                Mocks.createLog(
                        timeProvider,
                        id = Const.NO_ID,
                        remoteData = Mocks.createRemoteData(
                                timeProvider,
                                remoteId = 2
                        )
                )
        )
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor).deleteByLocalId(eq(1))
        verify(dbInteractor).deleteByRemoteId(eq(2))
    }

}