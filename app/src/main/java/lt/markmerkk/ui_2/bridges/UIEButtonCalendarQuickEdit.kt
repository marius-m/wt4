package lt.markmerkk.ui_2.bridges

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.StackPane
import lt.markmerkk.LogStorage
import lt.markmerkk.Strings
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.views.QuickEditView
import lt.markmerkk.validators.TimeChangeValidator
import lt.markmerkk.validators.TimeGap
import org.slf4j.LoggerFactory


class UIEButtonCalendarQuickEdit(
        private val node: ExternalSourceNode<StackPane>,
        private val strings: Strings,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider
) : UIElement<Node> {

    private val timeChangeValidator = TimeChangeValidator
    var selectLogId: Long = NO_ID
        private set

    private val viewQuickEdit = QuickEditView(object : QuickEditView.Listener {
        override fun moveForward(minutes: Int) {
            val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
            val newTimeGap = timeChangeValidator.moveForward(
                    TimeGap.from(
                            timeProvider.dateTimeFromMillis(simpleLog.start),
                            timeProvider.dateTimeFromMillis(simpleLog.end)
                    ),
                    minutes
            )
            val newSimpleLog = SimpleLogBuilder(simpleLog)
                    .setStart(newTimeGap.start.millis)
                    .setEnd(newTimeGap.end.millis)
                    .build()
            logStorage.update(newSimpleLog)
        }

        override fun moveBackward(minutes: Int) {
            val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
            val newTimeGap = timeChangeValidator.moveBackward(
                    TimeGap.from(
                            timeProvider.dateTimeFromMillis(simpleLog.start),
                            timeProvider.dateTimeFromMillis(simpleLog.end)
                    ),
                    minutes
            )
            val newSimpleLog = SimpleLogBuilder(simpleLog)
                    .setStart(newTimeGap.start.millis)
                    .setEnd(newTimeGap.end.millis)
                    .build()
            logStorage.update(newSimpleLog)
        }

    })

    init {
        node.rootNode().children.add(viewQuickEdit.root)
        StackPane.setAlignment(viewQuickEdit.root, Pos.TOP_RIGHT)
        StackPane.setMargin(viewQuickEdit.root, Insets(10.0, 20.0, 0.0, 0.0))
    }

    fun changeLogSelection(id: Long) {
        logger.debug("Select log with id = ${id}")
        this.selectLogId = id
    }

    fun changeLogSelectionToNoSelection() {
        logger.debug("No active log selected")
        this.selectLogId = NO_ID
    }

    override fun raw(): Node = viewQuickEdit.root

    override fun show() {
        viewQuickEdit.root.isVisible = true
        viewQuickEdit.root.isManaged = true
    }

    override fun hide() {
        viewQuickEdit.root.isVisible = false
        viewQuickEdit.root.isManaged = false
    }

    override fun reset() {}

    companion object {
        const val NO_ID = -1L
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}