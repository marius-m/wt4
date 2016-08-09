package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.mvp.UserSettings
import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.merger.*
import lt.markmerkk.utils.*
import net.rcarz.jiraclient.WorkLog
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import javax.inject.Named
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
            jiraClientProvider: JiraClientProvider,
            userSettings: UserSettings
    ): JiraSearchSubscriber {
        return JiraSearchSubscriberImpl(jiraClientProvider, userSettings)
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
    fun providesRemoteIssueMergeExecutor(
            executor: IExecutor
    ): RemoteIssueMergeExecutorImpl {
        return RemoteIssueMergeExecutorImpl(executor)
    }

    @Provides
    @Singleton
    fun providesRemoteLogMergeExecutor(
            executor: IExecutor
    ): RemoteLogMergeExecutorImpl {
        return RemoteLogMergeExecutorImpl(executor)
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
            dataStorage: BasicLogStorage,
            jiraClientProvider: JiraClientProvider,
            jiraSearchSubscriber: JiraSearchSubscriber,
            jiraWorklogSubscriber: JiraWorklogSubscriber
    ): JiraInteractor {
        return JiraInteractorImpl(
                localStorage = dataStorage,
                jiraClientProvider = jiraClientProvider,
                jiraSearchSubscriber = jiraSearchSubscriber,
                jiraWorklogSubscriber = jiraWorklogSubscriber,
                ioScheduler = Schedulers.computation()
        )
    }

    @Provides
    @Singleton
    fun providesRemoteMergeToolsProvider(
            remoteMergeClient: RemoteMergeClient,
            remoteLogMergeExecutor: RemoteLogMergeExecutorImpl
    ): RemoteMergeToolsProvider {
        return RemoteMergeToolsProviderImpl(
                remoteMergeClient = remoteMergeClient,
                remoteMergeExecutor = remoteLogMergeExecutor
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