package lt.markmerkk.mvp

import lt.markmerkk.utils.LogUtils

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 */
class SearchOpenPresenterImpl(
        private val view: SearchOpenMvp.View,
        private val interactor: HostServicesInteractor
) : SearchOpenMvp.Presenter {
    override fun open(input: String) {
        val link = LogUtils.validateTaskTitle(input)
        if (link.isEmpty()) return
        interactor.openExternalIssue(link)
    }

    override fun handleInputChange(input: String) {
        val validatedIssue = LogUtils.validateTaskTitle(input)
        if (!validatedIssue.isEmpty()) {
            view.showOpenButton()
        } else {
            view.hideOpenButton()
        }
    }

}