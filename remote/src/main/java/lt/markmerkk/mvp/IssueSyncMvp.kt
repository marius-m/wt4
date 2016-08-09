package lt.markmerkk.mvp

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
interface IssueSyncMvp {
    interface View {
        fun showProgress()
        fun hideProgress()
    }
    interface Presenter {
        fun onAttach()
        fun onDetach()

        fun sync()
    }
}