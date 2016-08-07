package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.merger.RemoteMergeExecutor
import lt.markmerkk.merger.RemoteMergeExecutorImpl
import lt.markmerkk.storage2.BasicLogStorage
import lt.markmerkk.storage2.database.interfaces.IExecutor
import lt.markmerkk.utils.*
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import javax.inject.Singleton

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
@Module
class SyncModule {

    @Provides
    @Singleton
    fun providesClientProvider(
            userSettings: UserSettings
    ): JiraClientProvider {
        return JiraClientProviderImpl(
                userSettings.host,
                userSettings.username,
                userSettings.password
        )
    }

    @Provides
    @Singleton
    fun providesSearchSubscriber(
            jiraClientProvider: JiraClientProvider
    ): JiraSearchSubsciber {
        return JiraSearchSubscriberImpl(jiraClientProvider)
    }

    @Provides
    @Singleton
    fun providesWorklogSubscriber(
            jiraClientProvider: JiraClientProvider
    ): JiraWorklogSubscriber {
        return JiraWorklogSubscriberImpl(jiraClientProvider)
    }

    @Provides
    @Singleton
    fun providesRemoteMergeExecutor(
            executor: IExecutor
    ): RemoteMergeExecutor {
        return RemoteMergeExecutorImpl(executor)
    }

    @Provides
    @Singleton
    fun providesInteractor(
            jiraClientProvider: JiraClientProvider,
            jiraSearchSubsciber: JiraSearchSubsciber,
            jiraWorklogSubscriber: JiraWorklogSubscriber
    ): JiraInteractor {
        return JiraInteractorImpl(
                jiraClientProvider = jiraClientProvider,
                jiraSearchSubsciber = jiraSearchSubsciber,
                jiraWorklogSubscriber = jiraWorklogSubscriber
        )
    }

    @Provides
    @Singleton
    fun providesRemoteMergeToolsProvider(
            remoteMergeExecutor: RemoteMergeExecutor
    ): RemoteMergeToolsProvider {
        return RemoteMergeToolsProviderImpl(
                mergeExecutor = remoteMergeExecutor
        )
    }

    @Provides
    fun provicesSyncController(
            settings: UserSettings,
            remoteMergeToolsProvider: RemoteMergeToolsProvider,
            dayProvider: DayProvider,
            lastUpdateController: LastUpdateController,
            jiraInteractor: JiraInteractor
    ): SyncController2 {
        return SyncController2(
                jiraInteractor = jiraInteractor,
                userSettings = settings,
                remoteMergeToolsProvider = remoteMergeToolsProvider,
                lastUpdateController = lastUpdateController,
                dayProvider = dayProvider,
                ioScheduler = Schedulers.computation(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
    }

}