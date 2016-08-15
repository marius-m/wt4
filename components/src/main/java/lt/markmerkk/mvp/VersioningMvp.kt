package lt.markmerkk.mvp

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
interface VersioningMvp {
    interface View {
        fun showProgress(progress: Float)
        fun showUpdateAvailable()
        fun showUpdateInProgress()
        fun showUpdateUnavailable()
    }
    interface Presenter {
        fun onAttach()
        fun onDetach()
    }
}