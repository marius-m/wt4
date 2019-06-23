package lt.markmerkk.ui_2.views.calendar_edit

class QuickEditContainerPresenter: QuickEditContract.ContainerPresenter {

    private var view: QuickEditContract.ContainerView? = null

    override fun onAttach(view: QuickEditContract.ContainerView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }
}