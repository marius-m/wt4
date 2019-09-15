package lt.markmerkk.ui_2.views.progress

interface ProgressContract {

    interface View {
        fun onAttach()
        fun onDetach()
        fun showProgress()
        fun hideProgress()
        fun changeLabel(syncData: String)
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun onClickSync()
        fun checkSyncDuration()
    }

}