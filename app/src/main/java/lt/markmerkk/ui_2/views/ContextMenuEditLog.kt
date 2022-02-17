package lt.markmerkk.ui_2.views

import com.jfoenix.svg.SVGGlyph
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.LogRepository
import lt.markmerkk.Strings
import lt.markmerkk.Tags
import lt.markmerkk.WTEventBus
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
    private val logRepository: LogRepository,
    private val eventBus: WTEventBus,
    private val editTypes: List<LogEditType>
) {
    val root: ContextMenu = ContextMenu()
            .apply {
                items.addAll(menuItemsFromEditTypes(editTypes, strings, graphics))
                setOnAction { event ->
                    val logEditType = LogEditType.valueOf((event.target as MenuItem).id)
                    val selectedLogs = selectedLogIds
                            .mapNotNull { logRepository.findByIdOrNull(it) }
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
        fun menuItemsFromEditTypes(
                logEditTypes: List<LogEditType>,
                strings: Strings,
                graphics: Graphics<SVGGlyph>
        ): List<MenuItem> {
            return logEditTypes.map {
                when (it) {
                    LogEditType.NEW -> MenuItem(
                            strings.getString("general_new"),
                            graphics.from(Glyph.NEW, Color.BLACK, 16.0, 16.0)
                    ).apply { id = LogEditType.NEW.name }
                    LogEditType.UPDATE -> MenuItem(
                            strings.getString("general_update"),
                            graphics.from(Glyph.UPDATE, Color.BLACK, 16.0, 16.0)
                    ).apply { id = LogEditType.UPDATE.name }
                    LogEditType.DELETE -> MenuItem(
                            strings.getString("general_delete"),
                            graphics.from(Glyph.DELETE, Color.BLACK, 12.0, 16.0)
                    ).apply { id = LogEditType.DELETE.name }
                    LogEditType.CLONE -> MenuItem(
                            strings.getString("general_clone"),
                            graphics.from(Glyph.CLONE, Color.BLACK, 16.0, 12.0)
                    ).apply { id = LogEditType.CLONE.name }
                    LogEditType.SPLIT -> MenuItem(
                            strings.getString("general_split"),
                            graphics.from(Glyph.SPLIT, Color.BLACK, 16.0, 12.0)
                    ).apply { id = LogEditType.SPLIT.name }
                    LogEditType.WEBLINK -> MenuItem(
                            strings.getString("general_weblink"),
                            graphics.from(Glyph.LINK, Color.BLACK, 14.0, 16.0)
                    ).apply { id = LogEditType.WEBLINK.name }
                    LogEditType.BROWSER -> MenuItem(
                            strings.getString("general_browser"),
                            graphics.from(Glyph.NEW, Color.BLACK, 16.0, 16.0)
                    ).apply { id = LogEditType.BROWSER.name }
                }
            }
        }
    }

}