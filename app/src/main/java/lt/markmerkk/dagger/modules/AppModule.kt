package lt.markmerkk.dagger.modules

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import dagger.Module
import dagger.Provides
import javafx.application.Application
import lt.markmerkk.*
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.migrations.*
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.repositories.CreditsRepository
import lt.markmerkk.tickets.JiraTicketSearch
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.*
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.tracker.GATracker
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.utils.tracker.NullTracker
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.versioner.VersionProvider
import lt.markmerkk.widgets.network.Api
import lt.markmerkk.widgets.versioner.VersionProviderImpl
import lt.markmerkk.worklogs.JiraWorklogInteractor
import lt.markmerkk.worklogs.WorklogApi
import javax.inject.Singleton

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
@Module
class AppModule(
        val application: Application,
        private val stageProperties: StageProperties
) {

    @Provides
    @Singleton
    fun providesSchedulersProvider(): SchedulerProvider = SchedulerProviderFx()


    @Provides
    @Singleton
    fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    fun providesSceneProperties(): StageProperties {
        return stageProperties
    }

    @Provides
    @Singleton
    fun providesEventBus(): EventBus {
        return EventBus()
    }

    @Provides
    @Singleton
    fun providesEventBusWrapper(eventBus: EventBus): WTEventBus {
        return WTEventBusImpl(eventBus)
    }

    @Provides
    @Singleton
    fun providesConfigPathProvider(): ConfigPathProvider {
        return ConfigPathProviderImpl(BuildConfig.debug)
    }

    @Provides
    @Singleton
    fun providesConfigSetSettings(configPathProvider: ConfigPathProvider): ConfigSetSettings {
        return ConfigSetSettingsImpl(configPathProvider)
    }

    @Provides
    @Singleton
    fun providesConfig(
            configPathProvider: ConfigPathProvider,
            configSetSettings: ConfigSetSettings
    ): Config {
        val config = Config(
                debug = BuildConfig.debug,
                versionName = BuildConfig.versionName,
                versionCode = BuildConfig.versionCode,
                gaKey = BuildConfig.gaKey,
                configPathProvider = configPathProvider,
                configSetSettings = configSetSettings
        )
        return config
    }

    @Provides
    @Singleton
    fun providerHostServiceInteractor(
            application: Application,
            userSettings: UserSettings
    ): HostServicesInteractor {
        return HostServicesInteractorImpl(application, userSettings)
    }

    @Provides
    @Singleton
    fun providesTracker(
            config: Config
    ): ITracker {
        if (config.debug) {
            return NullTracker()
        } else {
            return GATracker(config)
        }
    }

    @Provides
    @Singleton
    fun providesUserPrefs(
            config: Config
    ): UserSettings {
        return UserSettingsImpl(
                settings = AdvHashSettings(config)
        )
    }

    @Provides
    @Singleton
    fun providesDatabaseProvider(
            config: Config,
            timeProvider: TimeProvider
    ): DBConnProvider {
        val migrations = listOf(
                Migration0To1(
                        oldDatabase = DBConnProvider(
                                databaseName = "wt4_1.db",
                                databasePath = config.cfgPath
                        ),
                        newDatabase = DBConnProvider(
                                "wt4_2.db",
                                config.cfgPath
                        )
                ),
                Migration1To2(
                        oldDatabase = DBConnProvider(
                                databaseName = "wt4_1.db",
                                databasePath = config.cfgPath
                        ),
                        newDatabase = DBConnProvider(
                                "wt4_2.db",
                                config.cfgPath
                        ),
                        timeProvider = timeProvider
                ),
                Migration2To3(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                ),
                Migration3To4(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                ),
                Migration4To5(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                ),
                Migration5To6(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                ),
                Migration6To7(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                ),
                Migration7To8(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                ),
                Migration8To9(
                        database = DBConnProvider(
                                databaseName = "wt4_2.db",
                                databasePath = config.cfgPath
                        )
                )
        )
        val dbConnProvider = DBConnProvider(databaseName = "wt4_2.db", databasePath = config.cfgPath)
        MigrationsRunner(dbConnProvider)
                .run(migrations)
        return dbConnProvider
    }

    @Provides
    @Singleton
    fun providesDatabaseRepo(
            databaseProvider: DBConnProvider,
            timeProvider: TimeProvider
    ): TicketStorage {
        return TicketStorage(databaseProvider, timeProvider)
    }

    @Provides
    @Singleton
    fun providesWorklogStorage(
            connProvider: DBConnProvider,
            timeProvider: TimeProvider
    ): WorklogStorage {
        return WorklogStorage(timeProvider, DBInteractorLogJOOQ(connProvider, timeProvider))
    }

    @Provides
    @Singleton
    fun providesWorklogApi(
            jiraClientProvider: JiraClientProvider,
            userSettings: UserSettings,
            timeProvider: TimeProvider,
            ticketStorage: TicketStorage,
            worklogStorage: WorklogStorage
    ): WorklogApi {
        return WorklogApi(
                timeProvider,
                jiraClientProvider,
                JiraWorklogInteractor(
                        jiraClientProvider,
                        timeProvider,
                        userSettings
                ),
                ticketStorage,
                worklogStorage
        )
    }

    @Provides
    @Singleton
    fun providesTicketsNetworkRepo(
            ticketsDatabaseRepo: TicketStorage,
            jiraClientProvider: JiraClientProvider,
            userSettings: UserSettings
    ): TicketApi {
        return TicketApi(
                jiraClientProvider,
                JiraTicketSearch(),
                ticketsDatabaseRepo,
                userSettings
        )
    }

    @Provides
    @Singleton
    fun providesTimeProvider(): TimeProvider {
        return TimeProviderJfx()
    }

    @Provides
    @Singleton
    fun provideBasicLogStorage(
            worklogRepo: WorklogStorage,
            timeProvider: TimeProvider,
            schedulerProvider: SchedulerProvider,
            autoSyncWatcher: AutoSyncWatcher2
    ): LogStorage {
//        return LogStorageLegacy(dbExecutor, worklogRepo, timeProvider) // rename to restore old usage
        return LogStorage(worklogRepo, timeProvider, autoSyncWatcher)
    }

    @Provides
    @Singleton
    fun provideHourGlass2(
            timeProvider: TimeProvider,
            eventBus: WTEventBus
    ): HourGlass {
        return HourGlass(eventBus, timeProvider)
    }

    @Provides
    @Singleton
    fun provideDayProvider(logStorage: LogStorage): DayProvider {
        return DayProviderImpl(logStorage)
    }

    @Provides
    @Singleton
    fun provideStrings(): Strings {
        return StringsImpl()
    }

    @Provides
    @Singleton
    fun provideGraphics(): Graphics<SVGGlyph> {
        return GraphicsGlyph()
    }

    @Provides
    @Singleton
    fun provideActiveLogData(
            timeProvider: TimeProvider
    ): ActiveLogPersistence {
        return ActiveLogPersistence(timeProvider)
    }

    @Provides
    @Singleton
    fun provideResultDispatcher(): ResultDispatcher {
        return ResultDispatcher()
    }

    @Provides
    @Singleton
    fun provideLogChangeValidator(
            logStorage: LogStorage
    ): LogChangeValidator {
        return LogChangeValidator(logStorage)
    }

    @Provides
    @Singleton
    fun provideAccountAvailability(
            userSettings: UserSettings,
            jiraClientProvider: JiraClientProvider
    ): AccountAvailablility {
        if (BuildConfig.oauth) {
            return AccountAvailabilityOAuth(userSettings, jiraClientProvider)
        } else {
            return AccountAvailabilityBasic(userSettings, jiraClientProvider)
        }
    }

    @Provides
    @Singleton
    fun provideJiraLinkGenerator(
            accountAvailablility: AccountAvailablility
    ): JiraLinkGenerator {
        if (BuildConfig.oauth) {
            return JiraLinkGeneratorOAuth(view = null, accountAvailability = accountAvailablility)
        } else {
            return JiraLinkGeneratorBasic(view = null, accountAvailablility = accountAvailablility)
        }
    }

    @Provides
    @Singleton
    fun provideAutoSyncWatcher(
            eventBus: WTEventBus,
            timeProvider: TimeProvider,
            schedulerProvider: SchedulerProvider,
            accountAvailablility: AccountAvailablility
    ): AutoSyncWatcher2 {
        return AutoSyncWatcher2(
                timeProvider = timeProvider,
                eventBus = eventBus,
                accountAvailablility = accountAvailablility,
                ioScheduler = schedulerProvider.waitScheduler(),
                uiScheduler = schedulerProvider.ui()
        )
    }

    @Provides
    @Singleton
    fun provideCreditsRepository(): CreditsRepository {
        return CreditsRepository()
    }

    @Provides
    @Singleton
    fun provideVersionProvider(
            api: Api
    ): VersionProvider {
        return VersionProviderImpl(api)
    }

    @Provides
    @Singleton
    fun provideTicker(
            eventBus: WTEventBus,
            schedulerProvider: SchedulerProvider
    ): Ticker {
        return Ticker(
                eventBus,
                schedulerProvider.waitScheduler(),
                schedulerProvider.ui()
        )
    }

}