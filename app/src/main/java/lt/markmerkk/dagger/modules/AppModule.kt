package lt.markmerkk.dagger.modules

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import dagger.Module
import dagger.Provides
import javafx.application.Application
import lt.markmerkk.*
import lt.markmerkk.tickets.TicketsRepository
import lt.markmerkk.entities.database.interfaces.IExecutor
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
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import javax.inject.Singleton

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
@Module
class AppModule(
        val application: Application,
        val sceneProperties: StageProperties
) {

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    fun providesSceneProperties(): StageProperties {
        return sceneProperties
    }

    @Provides
    @Singleton
    fun providesEventBus(): EventBus {
        return EventBus()
    }


    @Provides
    @Singleton
    fun providesConfigPathProvider(): ConfigPathProvider {
        val debug = System.getProperty("release") == "false"
        return ConfigPathProviderImpl(debug)
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
        val debug = System.getProperty("release") == "false"
        val config = Config(
                debug = debug,
                versionName = System.getProperty("version_name"),
                versionCode = System.getProperty("version_code").toInt(),
                gaKey = System.getProperty("ga_key"),
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
    fun providesTicketsRepository(
            ticketsDatabaseRepo: TicketsDatabaseRepo,
            ticketsNetworkRepo: TicketsNetworkRepo,
            userSettings: UserSettings,
            timeProvider: TimeProvider
    ): TicketsRepository {
        return TicketsRepository(
                ticketsDatabaseRepo,
                ticketsNetworkRepo,
                userSettings,
                timeProvider
        )
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
        return TimeProvider()
    }

    @Provides
    @Singleton
    fun provideBasicLogStorage(dbExecutor: IExecutor): LogStorage { // todo : temp solution
        return LogStorage(dbExecutor)
    }

    @Provides
    @Singleton
    fun provideBasicIssueStorage(dbExecutor: IExecutor): IssueStorage {
        return IssueStorage(dbExecutor)
    }

    @Provides
    @Singleton
    fun provideHourGlass(): HourGlass {
        return HourGlass()
    }

    @Provides
    @Singleton
    fun provideKeepAliveInteractor(): KeepAliveInteractor {
        return KeepAliveInteractorImpl(
                uiSCheduler = JavaFxScheduler.getInstance(),
                ioScheduler = Schedulers.computation()
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
        return GraphicsImpl()
    }

}