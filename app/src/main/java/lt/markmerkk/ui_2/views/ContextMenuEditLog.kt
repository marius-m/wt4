package lt.markmerkk.ui_2.views

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
        private val eventBus: WTEventBus
) {
    val root: ContextMenu = ContextMenu()
            .apply {
                val updateItem = MenuItem(
                        strings.getString("general_update"),
                        graphics.from(Glyph.UPDATE, Color.BLACK, 16.0, 16.0)
                ).apply { id = LogEditType.UPDATE.name }
                val deleteItem = MenuItem(
                        strings.getString("general_delete"),
                        graphics.from(Glyph.DELETE, Color.BLACK, 12.0, 16.0)
                ).apply { id = LogEditType.DELETE.name }
                val cloneItem = MenuItem(
                        strings.getString("general_clone"),
                        graphics.from(Glyph.CLONE, Color.BLACK, 16.0, 12.0)
                ).apply { id = LogEditType.CLONE.name }
                val splitItem = MenuItem(
                        strings.getString("general_split"),
                        graphics.from(Glyph.SPLIT, Color.BLACK, 16.0, 12.0)
                ).apply { id = LogEditType.SPLIT.name }
                items.addAll(updateItem, deleteItem, cloneItem, splitItem)
                setOnAction { event ->
                    val logEditType = LogEditType.valueOf((event.target as MenuItem).id)
                    val simpleLog: SimpleLog? = logStorage.findByIdOrNull(logId)
                    if (simpleLog != null) {
                        eventBus.post(EventEditLog(logEditType, simpleLog))
                    } else {
                        logger.warn("Cannot find log with id $logId. Have you used `bindLog` before ?")
                    }
                }
            }
    private var logId = Const.NO_ID

    /**
     * Binds [SimpleLog] that is triggered for editing
     */
    fun bindLog(logId: Long) {
        this.logId = logId
    }

    fun unbindLog() {
        this.logId = Const.NO_ID
    }

    fun handleEditType(editType: LogEditType) {

    }

    companion object {
        val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}