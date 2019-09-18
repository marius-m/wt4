package lt.markmerkk.events

import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SimpleLog

class EventEditLog(
        val editType: LogEditType,
        val logs: List<SimpleLog>
): EventsBusEvent