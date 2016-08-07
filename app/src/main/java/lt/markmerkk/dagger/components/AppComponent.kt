package lt.markmerkk.dagger.components

import dagger.Component
import javafx.application.Application
import lt.markmerkk.Main
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.dagger.modules.SyncModule
import lt.markmerkk.utils.SyncController2
import javax.inject.Provider
import javax.inject.Singleton

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                SyncModule::class
        )
)
interface AppComponent {

    fun inject(application: Application)

    fun presenterComponent(): PresenterComponent

}