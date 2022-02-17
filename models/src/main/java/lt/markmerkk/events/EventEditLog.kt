package lt.markmerkk.events

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.LogEditType

class EventEditLog(
        val editType: LogEditType,
        val logs: List<Log>
): EventsBusEvent