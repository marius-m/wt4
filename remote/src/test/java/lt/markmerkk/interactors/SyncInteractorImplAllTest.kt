package lt.markmerkk.interactors

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.worklogs.WorklogApi
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

class SyncInteractorImplAllTest {

    val jiraInteractor: JiraInteractor = mock()
    val userSettings: UserSettings = mock()
    val logStorage: IDataStorage<SimpleLog> = mock()
    val remoteToolsProvider: RemoteMergeToolsProvider = mock()
    val dayProvider: DayProvider = mock()
    val autoUpdateInteractor: AutoUpdateInteractor = mock()
    val worklogApi: WorklogApi = mock()
    val worklogStorage: WorklogStorage = mock()

    private val timeProvider = TimeProviderTest()

    val sync = SyncInteractorImpl(
            jiraInteractor = jiraInteractor,
            userSettings = userSettings,
            logStorage = logStorage,
            remoteMergeToolsProvider = remoteToolsProvider,
            dayProvider = dayProvider,
            autoUpdateInteractor = autoUpdateInteractor,
            worklogApi = worklogApi,
            worklogStorage = worklogStorage,
            timeProvider = timeProvider,
            uiScheduler = Schedulers.immediate(),
            ioScheduler = Schedulers.immediate()
    )


    @Before
    fun setUp() {
        whenever(jiraInteractor.jiraLocalWorks())
                .thenReturn(Observable.empty())
        whenever(jiraInteractor.jiraRemoteWorks(any(), any()))
                .thenReturn(Observable.empty())

        doReturn("test_host").whenever(userSettings).host
        doReturn("test_user").whenever(userSettings).username
        doReturn("test_pass").whenever(userSettings).password

        doReturn(1000L).whenever(dayProvider).startDay()
        doReturn(2000L).whenever(dayProvider).endDay()
    }

    @Test
    fun valid_triggerDataChange() {
        // Assemble
        // Act
        sync.syncAll()

        // Assert
        verify(logStorage).notifyDataChange()
    }

    @Test
    fun error_triggerDataChange() {
        // Assemble
        // Act
        sync.syncAll()

        // Assert
        verify(logStorage).notifyDataChange()
    }
}