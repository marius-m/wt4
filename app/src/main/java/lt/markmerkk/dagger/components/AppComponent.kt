package lt.markmerkk.dagger.components

import dagger.Component
import lt.markmerkk.Main
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.dagger.modules.NetworkModule
import lt.markmerkk.dagger.modules.SyncModule
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidget
import lt.markmerkk.ui_2.views.progress.ProgressWidget
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import lt.markmerkk.widgets.DatePickerWidget
import lt.markmerkk.widgets.app_settings.AppSettingsWidget
import lt.markmerkk.widgets.clock.ClockWidget
import lt.markmerkk.widgets.main.MainWidget
import lt.markmerkk.widgets.calendar.CalendarWidget
import lt.markmerkk.widgets.credits.CreditsWidget
import lt.markmerkk.widgets.edit.LogDetailsSideDrawerWidget
import lt.markmerkk.widgets.export.ExportSampleWidget
import lt.markmerkk.widgets.export.ExportWidget
import lt.markmerkk.widgets.list.ListLogWidget
import lt.markmerkk.widgets.log_check.LogFreshnessWidget
import lt.markmerkk.widgets.settings.AccountSettingsOauthWidget
import lt.markmerkk.widgets.settings.AccountSettingsWidget
import lt.markmerkk.widgets.statistics.StatisticsWidget
import lt.markmerkk.widgets.tickets.*
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                SyncModule::class,
                NetworkModule::class
        )
)
interface AppComponent {
    fun inject(application: Main)
    fun inject(mainWidget: MainWidget)
    fun inject(ticketSideDrawerWidget: TicketSideDrawerWidget)
    fun inject(logDetailsSideDrawerWidget: LogDetailsSideDrawerWidget)
    fun inject(clockWidget: ClockWidget)
    fun inject(ticketProgressWidget: TicketProgressWidget)
    fun inject(accountSettings: AccountSettingsWidget)
    fun inject(datePickerWidget: DatePickerWidget)
    fun inject(calendarWidget: CalendarWidget)
    fun inject(listLogWidget: ListLogWidget)
    fun inject(ticketSplitWidget: TicketSplitWidget)
    fun inject(statisticsWidget: StatisticsWidget)
    fun inject(accountSettingsOauthWidget: AccountSettingsOauthWidget)
    fun inject(creditsWidget: CreditsWidget)
    fun inject(quickDateChangeWidget: QuickDateChangeWidget)
    fun inject(quickEditContainerWidget: QuickEditContainerWidget)
    fun inject(progressWidget: ProgressWidget)
    fun inject(ticketFilterWidget: TicketFilterSettingsWidget)
    fun inject(ticketBasicSearchWidget: TicketBasicSearchWidget)
    fun inject(appSettingsWidget: AppSettingsWidget)
    fun inject(logFreshnessWidget: LogFreshnessWidget)
    fun inject(exportWidget: ExportWidget)
    fun inject(exportSampleWidget: ExportSampleWidget)
}