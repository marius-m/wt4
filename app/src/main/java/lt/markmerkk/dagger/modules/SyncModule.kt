package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.IssueStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.merger.*
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
            localStorage: LogStorage,
            issueStorage: IssueStorage,
            jiraClientProvider: JiraClientProvider,
            jiraSearchSubscriber: JiraSearchSubscriber,
            jiraWorklogSubscriber: JiraWorklogSubscriber
    ): JiraInteractor {
        return JiraInteractorImpl(
                logStorage = localStorage,
                issueStorage = issueStorage,
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
            remoteLogMergeExecutor: RemoteLogMergeExecutorImpl,
            remoteIssueMergeExecutor: RemoteIssueMergeExecutorImpl
    ): RemoteMergeToolsProvider {
        return RemoteMergeToolsProviderImpl(
                remoteMergeClient = remoteMergeClient,
                remoteLogMergeExecutor = remoteLogMergeExecutor,
                remoteIssueMergeExecutor = remoteIssueMergeExecutor
        )
    }

    @Provides
    @Singleton
    fun provicesSyncController(
            settings: UserSettings,
            remoteMergeToolsProvider: RemoteMergeToolsProvider,
            dayProvider: DayProvider,
            jiraInteractor: JiraInteractor,
            logStorage: LogStorage,
            issueStorage: IssueStorage
    ): SyncInteractor {
        return SyncInteractorImpl(
                jiraInteractor = jiraInteractor,
                logStorage = logStorage,
                issueStorage = issueStorage,
                userSettings = settings,
                remoteMergeToolsProvider = remoteMergeToolsProvider,
                dayProvider = dayProvider,
                uiScheduler = JavaFxScheduler.getInstance()
        )
    }

}