package lt.markmerkk.utils

class CalendarFxEditRules(
        private val listener: Listener
) {

    private var isEditMode = false

    fun enable() {
        if (isEditMode) return
        listener.showIsEditMode()
        isEditMode = true
    }

    fun disable() {
        if (!isEditMode) return
        listener.hideIsEditMode()
        isEditMode = false
    }

    fun isInEditMode(): Boolean = isEditMode

    interface Listener {
        fun showIsEditMode()
        fun hideIsEditMode()
    }

}