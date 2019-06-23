package lt.markmerkk.ui_2.views

/**
 * Defines a view that can select a log by passing it's reference
 */
interface SelectableView {
    fun onSelectLog(logId: Long)
}