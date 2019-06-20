package lt.markmerkk.ui_2.views.progress

interface ProgressContract {

    interface View {
        fun onAttach()
        fun onDetach()
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun onClickSync()
    }

}