package lt.markmerkk

import javafx.scene.Node
import javafx.scene.paint.Color
import lt.markmerkk.entities.SyncStatus
import lt.markmerkk.entities.SyncStatus.ERROR
import lt.markmerkk.entities.SyncStatus.INVALID
import lt.markmerkk.entities.SyncStatus.IN_SYNC
import lt.markmerkk.entities.SyncStatus.WAITING_FOR_SYNC


fun Node.showIf(predicate: Boolean) {
    isVisible = predicate
    isManaged = predicate
}

fun SyncStatus.toColor(): Color {
    return when (this) {
        INVALID -> Color.TRANSPARENT
        IN_SYNC -> Color.GREEN
        ERROR -> Color.RED
        WAITING_FOR_SYNC -> Color.ORANGE
    }
}
