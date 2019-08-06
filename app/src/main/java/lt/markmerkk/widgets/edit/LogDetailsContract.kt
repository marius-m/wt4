package lt.markmerkk.widgets.edit

interface LogDetailsContract {
    interface View
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
    }
}