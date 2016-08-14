package lt.markmerkk.ui.version

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
interface VersioningMvp {
    interface View {}
    interface Presenter {
        fun onAttach()
        fun onDetach()
    }
}