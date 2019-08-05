package lt.markmerkk.dagger.components

import dagger.Component
import lt.markmerkk.Main
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.dagger.modules.SyncModule
import lt.markmerkk.di.GuiceModuleApp
import lt.markmerkk.widgets.MainWidget
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                SyncModule::class
        )
)
interface AppComponent {
    fun inject(appModuleguice: GuiceModuleApp)

    fun inject(application: Main)
    fun inject(mainWidget: MainWidget)

    fun presenterComponent(): PresenterComponent

}