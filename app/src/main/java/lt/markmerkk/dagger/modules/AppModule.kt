package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.*
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.KeepAliveInteractor
import lt.markmerkk.interactors.KeepAliveInteractorImpl
import lt.markmerkk.ui.version.UpdaterImpl
import lt.markmerkk.ui.version.VersioningInteractor
import lt.markmerkk.ui.version.VersioningMvp
import lt.markmerkk.ui.version.VersioningInteractorImpl
import lt.markmerkk.utils.*
import lt.markmerkk.utils.hourglass.HourGlass
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.io.InputStream
import java.util.*
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
                debug = System.getProperty("release") != "true",
                versionName = System.getProperty("version_name"),
                versionCode = System.getProperty("version_code").toInt(),
                gaKey = System.getProperty("ga_key")
        )
        return config
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
        return DBProdExecutor(
                config = config,
                settings = userSettings
        )
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
    fun provideVersioningPresenter(): VersioningInteractor {
        return VersioningInteractorImpl(
                updaterInteractor = UpdaterImpl(),
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