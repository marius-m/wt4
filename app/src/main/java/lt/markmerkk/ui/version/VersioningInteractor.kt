package lt.markmerkk.ui.version

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
interface VersioningInteractor {
    fun onAttach()
    fun onDetach()

    fun checkVersion()
}