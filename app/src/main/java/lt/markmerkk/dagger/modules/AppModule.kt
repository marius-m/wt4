package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.AutoSync2
import lt.markmerkk.DBProdExecutor
import lt.markmerkk.entities.IssueStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.utils.*
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.hourglass.KeepAliveController
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
    fun provideDayProvider(logStorage: LogStorage): DayProvider {
        return DayProviderImpl(logStorage)
    }

}