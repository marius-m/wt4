package lt.markmerkk.ui_2.views

/**
 * Defines a view that needs lifecycle events to work properly
 */
interface LifecycleView {
    fun onAttach()
    fun onDetach()
}