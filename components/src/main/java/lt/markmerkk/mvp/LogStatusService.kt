package lt.markmerkk.mvp

/**
 * Triggers and validates actions to the view
 * for showing log and its info
 */
interface LogStatusService {
    fun onAttach()
    fun onDetach()
    fun showWithId(logId: Long?)

    interface Listener {
        fun show(
                task: String,
                message: String
        )
        fun hide()
    }

}