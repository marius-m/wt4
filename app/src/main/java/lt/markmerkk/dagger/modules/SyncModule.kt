package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.UserSettings
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.*
import lt.markmerkk.merger.*
import lt.markmerkk.interactors.AuthService
import lt.markmerkk.tickets.JiraSearchSubscriber
import lt.markmerkk.tickets.JiraSearchSubscriberImpl
import javax.inject.Singleton

@Module
class SyncModule {

    @Provides
    @Singleton
    fun providesClientProvider(
            userSettings: UserSettings
    ): JiraClientProvider {
        return JiraClientProvider(userSettings)
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
            jiraClientProvider: JiraClientProvider,
            jiraSearchSubscriber: JiraSearchSubscriber,
            jiraWorklogSubscriber: JiraWorklogSubscriber
    ): JiraInteractor {
        return JiraInteractorImpl(
                logStorage = localStorage,
                jiraClientProvider = jiraClientProvider,
                jiraSearchSubscriber = jiraSearchSubscriber,
                jiraWorklogSubscriber = jiraWorklogSubscriber
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
                remoteLogMergeExecutor = remoteLogMergeExecutor
        )
    }

    @Provides
    @Singleton
    fun providesAutoUpdateInteractor(
            userSettings: UserSettings
    ): AutoUpdateInteractor {
        return AutoUpdateInteractorImpl(
                userSettings = userSettings
        )
    }

    @Provides
    @Singleton
    fun providesSyncInteractor(
            settings: UserSettings,
            remoteMergeToolsProvider: RemoteMergeToolsProvider,
            dayProvider: DayProvider,
            jiraInteractor: JiraInteractor,
            logStorage: LogStorage,
            autoUpdateInteractor: AutoUpdateInteractor,
            schedulerProvider: SchedulerProvider
    ): SyncInteractor {
        return SyncInteractorImpl(
                jiraInteractor = jiraInteractor,
                logStorage = logStorage,
                userSettings = settings,
                remoteMergeToolsProvider = remoteMergeToolsProvider,
                dayProvider = dayProvider,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui(),
                autoUpdateInteractor = autoUpdateInteractor
        )
    }

    @Provides
    @Singleton
    fun providesJiraAuthInteractor(
            jiraClientProvider: JiraClientProvider
    ): AuthService.AuthInteractor {
        return AuthInteractorImpl(
                jiraClientProvider
        )
    }

}