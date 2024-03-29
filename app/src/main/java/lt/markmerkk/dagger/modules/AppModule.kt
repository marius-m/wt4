package lt.markmerkk.dagger.modules

import com.google.common.eventbus.EventBus
import com.google.gson.Gson
import com.jfoenix.svg.SVGGlyph
import dagger.Module
import dagger.Provides
import javafx.application.Application
import lt.markmerkk.AccountAvailabilityBasic
import lt.markmerkk.AccountAvailabilityOAuth
import lt.markmerkk.AutoSyncWatcher2
import lt.markmerkk.BuildConfig
import lt.markmerkk.Config
import lt.markmerkk.ConfigPathProvider
import lt.markmerkk.DBConnProvider
import lt.markmerkk.DBInteractorLogJOOQ
import lt.markmerkk.FileInteractor
import lt.markmerkk.FileInteractorImpl
import lt.markmerkk.Graphics
import lt.markmerkk.GraphicsGlyph
import lt.markmerkk.HostServicesInteractorImpl
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.ActiveDisplayRepositoryDefault
import lt.markmerkk.LogFormatterStringRes
import lt.markmerkk.MigrationsRunner
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.SchedulerProvider
import lt.markmerkk.SchedulerProviderFx
import lt.markmerkk.Strings
import lt.markmerkk.StringsImpl
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.TimeProviderJfx
import lt.markmerkk.UserSettings
import lt.markmerkk.WTEventBus
import lt.markmerkk.WTEventBusImpl
import lt.markmerkk.WorklogStorage
import lt.markmerkk.export.WorklogExporter
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.migrations.Migration0To1
import lt.markmerkk.migrations.Migration1To2
import lt.markmerkk.migrations.Migration2To3
import lt.markmerkk.migrations.Migration3To4
import lt.markmerkk.migrations.Migration4To5
import lt.markmerkk.migrations.Migration5To6
import lt.markmerkk.migrations.Migration6To7
import lt.markmerkk.migrations.Migration7To8
import lt.markmerkk.migrations.Migration8To9
import lt.markmerkk.migrations.Migration9To10
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.repositories.CreditsRepository
import lt.markmerkk.repositories.ExternalResRepository
import lt.markmerkk.tickets.JiraTicketSearch
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.timecounter.WorkGoalDurationCalculator
import lt.markmerkk.timecounter.WorkGoalForecaster
import lt.markmerkk.timecounter.WorkGoalReporter
import lt.markmerkk.timecounter.WorkGoalReporterStringRes
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.AccountAvailablility
import lt.markmerkk.utils.AdvHashSettings
import lt.markmerkk.utils.ConfigSetSettings
import lt.markmerkk.utils.ConfigSetSettingsImpl
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.JiraLinkGeneratorBasic
import lt.markmerkk.utils.JiraLinkGeneratorOAuth
import lt.markmerkk.utils.Ticker
import lt.markmerkk.utils.UserSettingsImpl
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.hourglass.HourGlassImpl
import lt.markmerkk.utils.tracker.GATracker
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.utils.tracker.NullTracker
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.versioner.VersionProvider
import lt.markmerkk.widgets.dialogs.Dialogs
import lt.markmerkk.widgets.dialogs.DialogsExternal
import lt.markmerkk.widgets.dialogs.DialogsInternal
import lt.markmerkk.widgets.help.HelpResourceLoader
import lt.markmerkk.widgets.help.HelpWidgetFactory
import lt.markmerkk.widgets.help.ImgResourceLoader
import lt.markmerkk.widgets.log_check.LogFreshnessChecker
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
        return ConfigPathProvider(
                debug = BuildConfig.debug,
                versionCode = BuildConfig.versionCode,
                appFlavor = BuildConfig.flavor
        )
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
                appName = BuildConfig.NAME,
                appFlavor = BuildConfig.flavor,
                versionName = BuildConfig.versionName,
                versionCode = BuildConfig.versionCode,
                gaKey = BuildConfig.gaKey,
                cpp = configPathProvider,
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
        return HostServicesInteractorImpl(
                application,
                userSettings
        )
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
                isOauth = BuildConfig.oauth,
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
                ),
                Migration9To10(
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
    fun provideLogRepository(
        worklogRepo: WorklogStorage,
        timeProvider: TimeProvider,
        autoSyncWatcher: AutoSyncWatcher2,
        eventbus: WTEventBus
    ): ActiveDisplayRepository {
        return ActiveDisplayRepositoryDefault(worklogRepo, timeProvider, autoSyncWatcher, eventbus)
    }

    @Provides
    @Singleton
    fun provideHourGlass2(
            timeProvider: TimeProvider,
            eventBus: WTEventBus
    ): HourGlass {
        return HourGlassImpl(eventBus, timeProvider)
    }

    @Provides
    @Singleton
    fun provideStrings(): Strings {
        return StringsImpl()
    }

    @Provides
    @Singleton
    fun provideLogFormatterStringRes(strings: Strings): LogFormatterStringRes {
        return LogFormatterStringRes(strings)
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
        worklogStorage: WorklogStorage
    ): LogChangeValidator {
        return LogChangeValidator(worklogStorage)
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
            accountAvailablility: AccountAvailablility,
            userSettings: UserSettings
    ): AutoSyncWatcher2 {
        return AutoSyncWatcher2(
                timeProvider = timeProvider,
                eventBus = eventBus,
                accountAvailablility = accountAvailablility,
                userSettings = userSettings,
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

    @Provides
    @Singleton
    fun provideLogFreshnessChecker(
            worklogStorage: WorklogStorage,
            timeProvider: TimeProvider
    ): LogFreshnessChecker {
        return LogFreshnessChecker(
                worklogStorage,
                timeProvider
        )
    }

    @Provides
    @Singleton
    fun provideFileInteractor(): FileInteractor {
        return FileInteractorImpl()
    }

    @Provides
    @Singleton
    fun provideWorklogExporter(
            gson: Gson,
            fileInteractor: FileInteractor,
            timeProvider: TimeProvider
    ): WorklogExporter {
        return WorklogExporter(gson, fileInteractor, timeProvider)
    }

    @Provides
    @Singleton
    fun provideDialogsExternal(
        resultDispatcher: ResultDispatcher,
        strings: Strings,
    ): DialogsExternal {
        return DialogsExternal(resultDispatcher, strings)
    }

    @Provides
    @Singleton
    fun provideDialogsInternal(
        resultDispatcher: ResultDispatcher,
        strings: Strings,
    ): DialogsInternal {
        return DialogsInternal(resultDispatcher, strings)
    }

    @Provides
    @Singleton
    fun provideDialogs(
        dialogsExternal: DialogsExternal,
        dialogsInternal: DialogsInternal,
    ): Dialogs {
        return dialogsInternal
    }

    @Provides
    @Singleton
    fun provideWorkGoalReporter(
        strings: Strings,
    ): WorkGoalReporter {
        return WorkGoalReporter(
            workGoalForecaster = WorkGoalForecaster(),
            stringRes = WorkGoalReporterStringRes(strings = strings),
        )
    }

    @Provides
    @Singleton
    fun provideWorkGoalDurationCalculator(
        hourGlass: HourGlass,
        activeDisplayRepository: ActiveDisplayRepository,
    ): WorkGoalDurationCalculator {
        return WorkGoalDurationCalculator(
            hourGlass = hourGlass,
            activeDisplayRepository = activeDisplayRepository,
        )
    }
    @Provides
    @Singleton
    fun provideExternalResRepository(): ExternalResRepository {
        return ExternalResRepository()
    }

    @Provides
    @Singleton
    fun provideImgResLoader(
        externalResRepository: ExternalResRepository
    ): ImgResourceLoader {
        return ImgResourceLoader(
            externalResRepository = externalResRepository,
        )
    }

    @Provides
    @Singleton
    fun provideHelpResLoader(
        externalResRepository: ExternalResRepository
    ): HelpResourceLoader {
        return HelpResourceLoader(
            externalResRepository = externalResRepository,
        )
    }

    @Provides
    @Singleton
    fun provideHelpWidgetFactory(
        imgResLoader: ImgResourceLoader,
        helpResourceLoader: HelpResourceLoader,
    ): HelpWidgetFactory {
        return HelpWidgetFactory(
            imgResLoader = imgResLoader,
            helpResLoader = helpResourceLoader,
        )
    }

}