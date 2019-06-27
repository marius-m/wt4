package lt.markmerkk.events

class EventInflateDialog(val type: DialogType)

enum class DialogType {
    ACTIVE_CLOCK,
    LOG_EDIT,
    TICKET_SEARCH,
    TICKET_SPLIT,
    TICKET_MERGE,
    ;
}