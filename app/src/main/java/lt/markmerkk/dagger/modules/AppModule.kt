package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.KeepAliveInteractor
import lt.markmerkk.interactors.KeepAliveInteractorImpl
import lt.markmerkk.utils.VersionUpdaterImpl
import lt.markmerkk.interactors.VersioningInteractor
import lt.markmerkk.interactors.VersioningInteractorImpl
import lt.markmerkk.utils.AdvHashSettings
import lt.markmerkk.utils.DayProviderImpl
import lt.markmerkk.utils.UserSettingsImpl
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
class AppModule {

    @Provides
    @Singleton
    fun providesConfig(): Config {
        val config = Config(
                debug = System.getProperty("release") == "false",
                versionName = System.getProperty("version_name"),
                versionCode = System.getProperty("version_code").toInt(),
                gaKey = System.getProperty("ga_key")
        )
        return config
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
        if (config.debug) {
            return DBTestExecutor()
        } else {
            return DBProdExecutor(
                    config = config,
                    settings = userSettings
            )
        }
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
    fun provideVersioningPresenter(
            config: Config
    ): VersioningInteractor {
        return VersioningInteractorImpl(
                versionUpdaterInteractor = VersionUpdaterImpl(config),
                ioScheduler = Schedulers.computation(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
    }

    @Provides
    @Singleton
    fun provideDayProvider(logStorage: LogStorage): DayProvider {
        return DayProviderImpl(logStorage)
    }

}