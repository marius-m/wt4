package lt.markmerkk.widgets.main

class MainPresenter(): MainContract.Presenter {

    private var view: MainContract.View? = null

    override fun onAttach(view: MainContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun updateAutoSyncLock(
            isOpenLogDetails: Boolean,
            isOpeningLogDetails: Boolean,
            isOpenTickets: Boolean,
            isOpeningTickets: Boolean
    ): Boolean {
        val isLocked = isOpenLogDetails
                || isOpeningLogDetails
                || isOpenTickets
                || isOpeningTickets
        view?.onAutoSyncLockChange(isLocked = isLocked)
        return isLocked
    }

}