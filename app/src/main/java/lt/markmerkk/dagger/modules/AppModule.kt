package lt.markmerkk.dagger.modules

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import dagger.Module
import dagger.Provides
import javafx.application.Application
import lt.markmerkk.*
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.interactors.KeepAliveInteractor
import lt.markmerkk.interactors.KeepAliveInteractorImpl
import lt.markmerkk.migrations.Migration0To1
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.JiraTicketSearch
import lt.markmerkk.tickets.TicketsNetworkRepo
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.*
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.tracker.GATracker
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.utils.tracker.NullTracker
import lt.markmerkk.validators.LogChangeValidator
import javax.inject.Singleton

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
    fun providesEventBus(): WTEventBus {
        return WTEventBusImpl(EventBus())
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
    fun providesDbExecutor(
            config: Config,
            userSettings: UserSettings
    ): IExecutor {
//        if (config.debug) {
//            return DBTestExecutor()
//        }
        // App path will be different for debug anyway
        return DBProdExecutor(
                config = config,
                settings = userSettings
        )
    }

    @Provides
    @Singleton
    fun providesDatabaseRepo(): TicketsDatabaseRepo {
        val migrations = listOf(
                Migration0To1(oldDatabase = DBConnProvider("wt4_1.db"))
        )
        val database = DBConnProvider("wt4_2.db")
        return TicketsDatabaseRepo(database, migrations)
    }

    @Provides
    @Singleton
    fun providesTicketsNetworkRepo(
            ticketsDatabaseRepo: TicketsDatabaseRepo,
            jiraClientProvider: JiraClientProvider,
            userSettings: UserSettings
    ): TicketsNetworkRepo {
        return TicketsNetworkRepo(
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
    fun provideBasicLogStorage(dbExecutor: IExecutor): LogStorage { // todo : temp solution
        return LogStorage(dbExecutor)
    }

    @Provides
    @Singleton
    fun provideHourGlass(): HourGlass {
        return HourGlass()
    }

    @Provides
    @Singleton
    fun provideKeepAliveInteractor(
            schedulerProvider: SchedulerProvider
    ): KeepAliveInteractor {
        return KeepAliveInteractorImpl(
                uiSCheduler = schedulerProvider.ui(),
                waitScheduler = schedulerProvider.waitScheduler()
        )
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

}