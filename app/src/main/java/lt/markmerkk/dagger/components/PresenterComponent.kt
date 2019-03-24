package lt.markmerkk.dagger.components

import dagger.Subcomponent
import lt.markmerkk.dagger.scopes.PerPresenterScope
import lt.markmerkk.ui.day.CalendarPresenter
import lt.markmerkk.ui.display.DisplayLogPresenter
import lt.markmerkk.ui.graphs.GraphsFxPresenter
import lt.markmerkk.ui.settings.SettingsPresenter
import lt.markmerkk.ui.status.StatusPresenter
import lt.markmerkk.ui.update.UpdateLogPresenter
import lt.markmerkk.ui.version.VersionPresenter
import lt.markmerkk.ui.week.WeekPresenter
import lt.markmerkk.ui_2.*

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
@PerPresenterScope
@Subcomponent
interface PresenterComponent {

    fun inject(statusPresenter: StatusPresenter)

    fun inject(displayLogPresenter: DisplayLogPresenter)

    fun inject(weekPresenter: WeekPresenter)

    fun inject(updateLogPresenter: UpdateLogPresenter)

    fun inject(settingsPresenter: SettingsPresenter)

    fun inject(presenter: VersionPresenter)

    fun inject(presenter: GraphsFxPresenter)

    fun inject(presenter: MainPresenter2)

    fun inject(presenter: CalendarPresenter)

    fun inject(dialog: DisplaySelectDialogController)

    fun inject(dialog: LogEditController)

    fun inject(dialog: CurrentDayDialogController)

    fun inject(dialog: ClockEditController)

    fun inject(dialog: StatisticsController)

    fun inject(statusController: LogStatusController)

    fun inject(settingsController: SettingsController)

    fun inject(profilesController: ProfilesController)

    fun inject(ticketsController: TicketsController)

}