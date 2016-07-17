package lt.markmerkk.dagger.components

import dagger.Subcomponent
import lt.markmerkk.dagger.scopes.PerPresenterScope
import lt.markmerkk.ui.MainPresenter
import lt.markmerkk.ui.clock.ClockPresenter
import lt.markmerkk.ui.display.DisplayLogPresenter
import lt.markmerkk.ui.settings.SettingsPresenter
import lt.markmerkk.ui.status.StatusPresenter
import lt.markmerkk.ui.update.UpdateLogPresenter
import lt.markmerkk.ui.week.WeekPresenter

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
@PerPresenterScope
@Subcomponent
interface PresenterComponent {

    fun inject(mainPresenter: MainPresenter)

    fun inject(statusPresenter: StatusPresenter)

    fun inject(clockPresenter: ClockPresenter)

    fun inject(displayLogPresenter: DisplayLogPresenter)

    fun inject(weekPresenter: WeekPresenter)

    fun inject(updateLogPresenter: UpdateLogPresenter)

    fun inject(settingsPresenter: SettingsPresenter)

}