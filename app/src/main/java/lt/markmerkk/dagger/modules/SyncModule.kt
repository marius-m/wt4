package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.merger.RemoteMergeExecutor
import lt.markmerkk.merger.RemoteMergeExecutorImpl
import lt.markmerkk.mvp.UserSettings
import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.merger.RemoteMergeClient
import lt.markmerkk.merger.RemoteMergeClientImpl
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
        return JiraClientProviderImpl(userSettings)
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
    fun providesRemoteMergeClient(
            jiraClientProvider: JiraClientProvider
    ): RemoteMergeClient {
        return RemoteMergeClientImpl(jiraClientProvider)
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
            remoteMergeClient: RemoteMergeClient,
            remoteMergeExecutor: RemoteMergeExecutor
    ): RemoteMergeToolsProvider {
        return RemoteMergeToolsProviderImpl(
                remoteMergeClient = remoteMergeClient,
                remoteMergeExecutor = remoteMergeExecutor
        )
    }

    @Provides
    fun provicesSyncController(
            settings: UserSettings,
            remoteMergeToolsProvider: RemoteMergeToolsProvider,
            dayProvider: DayProvider,
            lastUpdateController: LastUpdateController,
            jiraInteractor: JiraInteractor,
            jiraClientProvider: JiraClientProvider,
            logStorage: BasicLogStorage
    ): SyncController2 {
        return SyncController2(
                jiraClientProvider = jiraClientProvider,
                jiraInteractor = jiraInteractor,
                logStorage = logStorage,
                userSettings = settings,
                remoteMergeToolsProvider = remoteMergeToolsProvider,
                lastUpdateController = lastUpdateController,
                dayProvider = dayProvider,
                ioScheduler = Schedulers.computation(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
    }

}