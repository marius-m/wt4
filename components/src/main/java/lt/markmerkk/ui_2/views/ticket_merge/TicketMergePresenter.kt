package lt.markmerkk.ui_2.views.ticket_merge

class TicketMergePresenter: TicketMergeContract.Presenter {

    private var view: TicketMergeContract.View? = null

    override fun onAttach(view: TicketMergeContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }
}