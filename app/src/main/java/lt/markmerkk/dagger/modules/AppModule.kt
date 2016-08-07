package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.AutoSync2
import lt.markmerkk.DBProdExecutor
import lt.markmerkk.JiraInteractor
import lt.markmerkk.storage2.BasicLogStorage
import lt.markmerkk.storage2.IDataStorage
import lt.markmerkk.storage2.SimpleLog
import lt.markmerkk.storage2.database.interfaces.IExecutor
import lt.markmerkk.utils.*
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.hourglass.KeepAliveController
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
        return UserSettingsImpl()
    }

    @Provides
    @Singleton
    fun providesDbExecutor(): IExecutor {
        return DBProdExecutor()
    }

    @Provides
    @Singleton
    fun providesLogStorage(dbExecutor: IExecutor): IDataStorage<SimpleLog> {
        return BasicLogStorage(dbExecutor)
    }

    @Provides
    @Singleton
    fun provideBasicLogStorage(genericDataStorage: IDataStorage<SimpleLog>): BasicLogStorage { // todo : temp solution
        return genericDataStorage as BasicLogStorage
    }

    @Provides
    @Singleton
    fun providesLastUpdateController(userSettings: UserSettings): LastUpdateController {
        return LastUpdateControllerImpl(userSettings)
    }

    @Provides
    @Singleton
    fun provideHourGlass(): HourGlass {
        return HourGlass()
    }

    @Provides
    @Singleton
    fun provideKeepAliveController(): KeepAliveController {
        return KeepAliveController()
    }

    @Provides
    @Singleton
    fun provideAutoSync(): AutoSync2 {
        return AutoSync2()
    }

    @Provides
    @Singleton
    fun provideVersionController(): VersionController {
        return VersionController()
    }

    @Provides
    @Singleton
    fun provideDayProvider(logStorage: BasicLogStorage): DayProvider {
        return DayProviderImpl(logStorage)
    }

}