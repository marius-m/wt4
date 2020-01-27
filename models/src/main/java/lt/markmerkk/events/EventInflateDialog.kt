package lt.markmerkk.events

class EventInflateDialog(val type: DialogType): EventsBusEvent

enum class DialogType {
    ACTIVE_CLOCK,
    LOG_EDIT,
    TICKET_SEARCH,
    TICKET_SPLIT,
    ;
}