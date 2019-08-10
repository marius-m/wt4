package lt.markmerkk.dagger.components

import dagger.Component
import lt.markmerkk.Main
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.dagger.modules.SyncModule
import lt.markmerkk.widgets.clock.ClockWidget
import lt.markmerkk.widgets.MainWidget
import lt.markmerkk.widgets.edit.LogDetailsWidget
import lt.markmerkk.widgets.settings.AccountSettingsWidget
import lt.markmerkk.widgets.tickets.TicketProgressWidget
import lt.markmerkk.widgets.tickets.TicketWidget
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
    fun inject(mainWidget: MainWidget)
    fun inject(ticketWidget: TicketWidget)
    fun inject(logDetailsWidget: LogDetailsWidget)
    fun inject(clockWidget: ClockWidget)
    fun inject(ticketProgressWidget: TicketProgressWidget)
    fun inject(accountSettings: AccountSettingsWidget)

    fun presenterComponent(): PresenterComponent

}