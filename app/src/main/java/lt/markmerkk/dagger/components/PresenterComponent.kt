package lt.markmerkk.dagger.components

import dagger.Subcomponent
import lt.markmerkk.dagger.scopes.PerPresenterScope
import lt.markmerkk.ui.day.CalendarPresenter
import lt.markmerkk.ui.display.DisplayLogPresenter
import lt.markmerkk.ui.graphs.GraphsFxPresenter
import lt.markmerkk.ui.status.StatusPresenter
import lt.markmerkk.ui.version.VersionPresenter
import lt.markmerkk.ui_2.*

@PerPresenterScope
@Subcomponent
interface PresenterComponent {

    fun inject(statusPresenter: StatusPresenter)

    fun inject(displayLogPresenter: DisplayLogPresenter)

    fun inject(presenter: VersionPresenter)

    fun inject(presenter: GraphsFxPresenter)

    fun inject(presenter: CalendarPresenter)

    fun inject(dialog: DisplaySelectDialogController)

    fun inject(dialog: CurrentDayDialogController)

    fun inject(dialog: StatisticsController)

    fun inject(statusController: LogStatusController)

    fun inject(settingsController: SettingsController)

    fun inject(profilesController: ProfilesController)

    fun inject(ticketSplitController: TicketSplitController)

    fun inject(ticketMergeController: TicketMergeController)

}