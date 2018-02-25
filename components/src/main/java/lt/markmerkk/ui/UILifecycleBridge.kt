package lt.markmerkk.ui

/**
 * Encapsulates logic for the controller by its feature
 * that must have a lifecycle to work properly
 */
interface UILifecycleBridge {
    fun onAttach()
    fun onDetach()
}