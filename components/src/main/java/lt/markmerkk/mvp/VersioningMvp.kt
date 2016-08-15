package lt.markmerkk.mvp

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
interface VersioningMvp {
    interface View {
        fun showProgress(progress: Float)
    }
    interface Presenter {
        fun onAttach()
        fun onDetach()
    }
}