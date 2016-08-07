package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.interfaces.IRemoteLoadListener
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class SyncController2SyncTest {
    val settings: UserSettings = mock()
    val lastUpdateController: LastUpdateController = mock()
    val dayProvider: DayProvider = mock()
    val jiraInteractor: JiraInteractor = mock()
    val remoteMergeToolsProvider: RemoteMergeToolsProvider = mock()

    val controller = SyncController2(
            jiraInteractor = jiraInteractor,
            userSettings = settings,
            remoteMergeToolsProvider = remoteMergeToolsProvider,
            lastUpdateController = lastUpdateController,
            dayProvider = dayProvider,
            uiScheduler = Schedulers.immediate(),
            ioScheduler = Schedulers.immediate()
    )

    @Before
    fun setUp() {
        doReturn(Observable.empty<List<JiraWork>>()).whenever(jiraInteractor).jiraWorks(any(), any())
        doReturn("test_host").whenever(settings).host
        doReturn("test_user").whenever(settings).username
        doReturn("test_pass").whenever(settings).password
    }

    @Test
    fun sync_triggerLoadings() {
        val remoteLoadingListener: IRemoteLoadListener = mock()
        controller.addLoadingListener(remoteLoadingListener)

        controller.sync(
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        verify(remoteLoadingListener).onLoadChange(true)
        verify(remoteLoadingListener).onLoadChange(false)
    }

//    @Test
//    fun validResuult_triggerMerge() {
//        reset(jiraInteractor)
//        val validWorks = Observable.just(
//                listOf(
//                        JiraWork()
//                )
//        )
//        doReturn(validWorks).whenever(jiraInteractor).jiraWorks(any(), any())
//
//        controller.sync(
//                Schedulers.immediate(),
//                Schedulers.immediate()
//        )
//
//        verify(dbExecutor).execute(any())
//    }

}