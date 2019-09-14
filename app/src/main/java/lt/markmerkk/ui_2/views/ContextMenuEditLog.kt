package lt.markmerkk.ui_2.views

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.events.EventEditLog
import org.slf4j.LoggerFactory

/**
 * Represents a context menu that is reused throughout the app
 */
class ContextMenuEditLog(
        private val strings: Strings,
        private val graphics: Graphics<SVGGlyph>,
        private val logStorage: LogStorage,
        private val eventBus: EventBus
) {
    val root: ContextMenu = ContextMenu()
            .apply {
                items.addAll(
                        MenuItem(
                                strings.getString("general_update"),
                                graphics.from(Glyph.UPDATE, Color.BLACK, 16.0, 16.0)
                        ).apply { id = LogEditType.UPDATE.name },
                        MenuItem(
                                strings.getString("general_delete"),
                                graphics.from(Glyph.DELETE, Color.BLACK, 12.0, 16.0)
                        ).apply { id = LogEditType.DELETE.name },
                        MenuItem(
                                strings.getString("general_clone"),
                                graphics.from(Glyph.CLONE, Color.BLACK, 16.0, 12.0)
                        ).apply { id = LogEditType.CLONE.name },
                        MenuItem(
                                strings.getString("general_split"),
                                graphics.from(Glyph.SPLIT, Color.BLACK, 16.0, 12.0)
                        ).apply { id = LogEditType.SPLIT.name }
                        // Disabled for incomplete feature
//                        MenuItem(
//                                strings.getString("general_merge"),
//                                graphics.from(Glyph.MERGE, Color.BLACK, 16.0, 16.0)
//                        ).apply { id = LogEditType.MERGE.name }
                )
                setOnAction { event ->
                    val logEditType = LogEditType.valueOf((event.target as MenuItem).id)
                    val selectedLogs = selectedLogIds
                            .mapNotNull { logStorage.findByIdOrNull(it) }
                    eventBus.post(EventEditLog(logEditType, selectedLogs))
                }
            }
    private var selectedLogIds = emptyList<Long>()

    /**
     * Binds [SimpleLog] that is triggered for editing
     */
    fun bindLogs(logIds: List<Long>) {
        selectedLogIds = logIds
    }

    companion object {
        val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}