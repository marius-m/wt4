package lt.markmerkk

import com.nhaarman.mockitokotlin2.*
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
        doReturn(false).whenever(dbInteractor).isWorklogExistLocally(any())
        doReturn(false).whenever(dbInteractor).isWorklogExistRemotely(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(log)
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).delete(any())
    }

    @Test
    fun insertLocalLog_existOnlyAsLocal() {
        // Assemble
        val log = Mocks.createLog(timeProvider, id = 1)
        doReturn(true).whenever(dbInteractor).isWorklogExistLocally(any())
        doReturn(false).whenever(dbInteractor).isWorklogExistRemotely(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor).update(log)
        verify(dbInteractor, never()).delete(any())
    }

//    @Test // cannot happen
//    fun insertLocalLog_existLocal_existRemote() {
//        // Assemble
//        val log = Mocks.createLog(
//                timeProvider,
//                id = 1
//        )
//        doReturn(true).whenever(dbInteractor).isWorklogExistLocally(any())
//        doReturn(true).whenever(dbInteractor).isWorklogExistRemotely(any())
//
//        // Act
//        worklogStorage.insertOrUpdateSync(log)
//
//        // Assert
//        verify(dbInteractor).insert(
//                Mocks.createLog(
//                        timeProvider,
//                        id = Const.NO_ID,
//                        remoteData = null
//                )
//        )
//        verify(dbInteractor).update(
//                Mocks.createLog(
//                        timeProvider,
//                        id = 1,
//                        remoteData = Mocks.createRemoteData(
//                                timeProvider,
//                                remoteId = 2,
//                                isDeleted = true
//                        )
//                )
//        )
//        verify(dbInteractor, never()).delete(any())
//    }

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
        doReturn(false).whenever(dbInteractor).isWorklogExistLocally(any())
        doReturn(false).whenever(dbInteractor).isWorklogExistRemotely(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor).insert(log)
        verify(dbInteractor, never()).update(any())
        verify(dbInteractor, never()).delete(any())
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
        doReturn(true).whenever(dbInteractor).isWorklogExistLocally(any())
        doReturn(false).whenever(dbInteractor).isWorklogExistRemotely(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor).update(log)
        verify(dbInteractor, never()).delete(any())
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
        doReturn(false).whenever(dbInteractor).isWorklogExistLocally(any())
        doReturn(true).whenever(dbInteractor).isWorklogExistRemotely(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(log)
        verify(dbInteractor).update(log)
        verify(dbInteractor, never()).delete(any())
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
        doReturn(true).whenever(dbInteractor).isWorklogExistLocally(any())
        doReturn(true).whenever(dbInteractor).isWorklogExistRemotely(any())

        // Act
        worklogStorage.insertOrUpdateSync(log)

        // Assert
        verify(dbInteractor, never()).insert(any())
        verify(dbInteractor).update(any())
        verify(dbInteractor, never()).delete(any())
    }

}