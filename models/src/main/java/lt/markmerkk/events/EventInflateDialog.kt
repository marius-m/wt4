package lt.markmerkk.events

class EventInflateDialog(val type: DialogType)

enum class DialogType {
    ACTIVE_CLOCK,
    LOG_EDIT,
    ;
}