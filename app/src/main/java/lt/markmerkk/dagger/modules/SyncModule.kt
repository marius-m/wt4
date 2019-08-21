package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.UserSettings
import lt.markmerkk.LogStorage
import lt.markmerkk.interactors.*
import lt.markmerkk.interactors.AuthService
import lt.markmerkk.worklogs.WorklogApi
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
            dayProvider: DayProvider,
            logStorage: LogStorage,
            autoUpdateInteractor: AutoUpdateInteractor,
            schedulerProvider: SchedulerProvider,
            timeProvider: TimeProvider,
            worklogStorage: WorklogStorage,
            worklogApi: WorklogApi
    ): SyncInteractor {
        return SyncInteractorImpl(
                logStorage = logStorage,
                dayProvider = dayProvider,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui(),
                autoUpdateInteractor = autoUpdateInteractor,
                timeProvider = timeProvider,
                worklogStorage = worklogStorage,
                worklogApi = worklogApi
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