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
        return if (BuildConfig.oauth) {
            JiraClientProviderOauth(userSettings)
        } else {
            JiraClientProviderBasic(userSettings)
        }
    }

    @Provides
    @Singleton
    fun providesSyncInteractor(
            dayProvider: DayProvider,
            logStorage: LogStorage,
            schedulerProvider: SchedulerProvider,
            timeProvider: TimeProvider,
            worklogStorage: WorklogStorage,
            worklogApi: WorklogApi,
            jiraClientProvider: JiraClientProvider,
            userSettings: UserSettings,
            jiraBasicApi: JiraBasicApi
    ): SyncInteractor {
        return SyncInteractorImpl(
                logStorage = logStorage,
                dayProvider = dayProvider,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui(),
                timeProvider = timeProvider,
                jiraClientProvider = jiraClientProvider,
                worklogStorage = worklogStorage,
                worklogApi = worklogApi,
                userSettings = userSettings,
                jiraBasicApi = jiraBasicApi
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