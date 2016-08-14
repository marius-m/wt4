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
import javax.inject.Singleton

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun providesUserPrefs(): UserSettings {
        return UserSettingsImpl(
                settings = AdvHashSettings()
        )
    }

    @Provides
    @Singleton
    fun providesDbExecutor(): IExecutor {
        return DBProdExecutor()
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