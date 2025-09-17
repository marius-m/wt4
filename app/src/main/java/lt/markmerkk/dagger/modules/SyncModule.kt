package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.interactors.*
import lt.markmerkk.worklogs.WorklogApi
import javax.inject.Singleton

@Module
class SyncModule {

    @Provides
    @Singleton
    fun providesClientProvider(
            userSettings: UserSettings
    ): JiraClientProvider {
        return JiraClientProviderBasic(userSettings)
    }

    @Provides
    @Singleton
    fun providesSyncInteractor(
        schedulerProvider: SchedulerProvider,
        timeProvider: TimeProvider,
        worklogStorage: WorklogStorage,
        worklogApi: WorklogApi,
        jiraClientProvider: JiraClientProvider,
        userSettings: UserSettings,
        jiraBasicApi: JiraBasicApi,
        activeDisplayRepository: ActiveDisplayRepository
    ): SyncInteractor {
        return SyncInteractorImpl(
            ioScheduler = schedulerProvider.io(),
            uiScheduler = schedulerProvider.ui(),
            timeProvider = timeProvider,
            jiraClientProvider = jiraClientProvider,
            worklogStorage = worklogStorage,
            worklogApi = worklogApi,
            userSettings = userSettings,
            jiraBasicApi = jiraBasicApi,
            activeDisplayRepository = activeDisplayRepository
        )
    }

    @Provides
    @Singleton
    fun providesJiraBasicApi(
            jiraClientProvider: JiraClientProvider
    ): JiraBasicApi {
        return JiraBasicApi(jiraClientProvider)
    }

    @Provides
    @Singleton
    fun providesJiraAuthInteractor(
            jiraClientProvider: JiraClientProvider,
            jiraBasicApi: JiraBasicApi,
            userSettings: UserSettings
    ): AuthService.AuthInteractor {
        return AuthInteractorImpl(
                jiraClientProvider,
                jiraBasicApi,
                userSettings
        )
    }

}