package lt.markmerkk.dagger.components

import dagger.Component
import lt.markmerkk.Main
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.dagger.modules.SyncModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                SyncModule::class
        )
)
interface AppComponent {

    fun inject(application: Main)

    fun presenterComponent(): PresenterComponent

}