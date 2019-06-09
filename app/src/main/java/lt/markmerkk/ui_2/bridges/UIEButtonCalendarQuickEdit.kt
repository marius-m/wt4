package lt.markmerkk.ui_2.bridges

import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.validators.TimeChangeValidator
import lt.markmerkk.validators.TimeGap
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.hgrow
import tornadofx.vgrow


class UIEButtonCalendarQuickEdit(
        private val node: ExternalSourceNode<StackPane>,
        private val strings: Strings,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider,
        private val graphics: Graphics<SVGGlyph>
) : UIElement<Node>, QuickEditActionChangeListener {

    private val mainContainer = VBox()
    private val timeChangeValidator = TimeChangeValidator
    private val quickEditActions = setOf(
            QuickEditAction.MOVE,
            QuickEditAction.SHRINK,
            QuickEditAction.EXPAND
    )
    var selectLogId: Long = NO_ID
        private set

    private val viewQuickEditWidgetMove = QuickEditWidgetMove(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            containerWidth = CONTAINER_WIDTH,
            graphics = graphics,
            listener = object : QuickEditWidgetMove.Listener {
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

    private val viewQuickEditWidgetShrink = QuickEditWidgetShrink(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            containerWidth = CONTAINER_WIDTH,
            graphics = graphics,
            listener = object : QuickEditWidgetShrink.Listener {
                override fun shrinkFromStart(minutes: Int) {
                    val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
                    val newTimeGap = timeChangeValidator.shrinkFromStart(
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

                override fun shrinkFromEnd(minutes: Int) {
                    val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
                    val newTimeGap = timeChangeValidator.shrinkFromEnd(
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

    private val viewQuickEditWidgetExpand = QuickEditWidgetExpand(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            containerWidth = CONTAINER_WIDTH,
            graphics = graphics,
            listener = object : QuickEditWidgetExpand.Listener {
                override fun expandToStart(minutes: Int) {
                    val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
                    val newTimeGap = timeChangeValidator.expandToStart(
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

                override fun expandToEnd(minutes: Int) {
                    val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
                    val newTimeGap = timeChangeValidator.expandToEnd(
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

    override fun onActiveActionChange(quickEditAction: QuickEditAction) {
        viewsQuickEditWidgets
                .filter { it.value is QuickEditChangableAction }
                .map { it.value as QuickEditChangableAction }
                .forEach { it.changeActiveAction(quickEditAction) }
        val viewActive = viewsQuickEditWidgets.getValue(quickEditAction)
        viewActive.root.isVisible = true
        viewActive.root.isManaged = true
        val inactiveViews = viewsQuickEditWidgets
                .filter { it.key != quickEditAction }
                .map { it.value }
        inactiveViews.forEach {
            it.root.isVisible = false
            it.root.isManaged = false
        }
    }


    private val viewsQuickEditWidgets = mapOf<QuickEditAction, View>(
            QuickEditAction.MOVE to viewQuickEditWidgetMove,
            QuickEditAction.SHRINK to viewQuickEditWidgetShrink,
            QuickEditAction.EXPAND to viewQuickEditWidgetExpand
    )

    init {
        viewsQuickEditWidgets.values.forEach { mainContainer.children.add(it.root) }
        mainContainer.maxWidth = CONTAINER_WIDTH
        mainContainer.maxHeight = CONTAINER_WIDTH
        mainContainer.vgrow = Priority.NEVER
        mainContainer.hgrow = Priority.NEVER
        node.rootNode().children.add(mainContainer)
        StackPane.setAlignment(mainContainer, Pos.TOP_RIGHT)
        StackPane.setMargin(mainContainer, Insets(10.0, 20.0, 0.0, 0.0))
        onActiveActionChange(QuickEditAction.MOVE)
    }

    fun changeLogSelection(id: Long) {
        this.selectLogId = id
    }

    fun changeLogSelectionToNoSelection() {
        this.selectLogId = NO_ID
    }

    override fun raw(): Node = mainContainer

    override fun show() {
        mainContainer.isVisible = true
        mainContainer.isManaged = true
    }

    override fun hide() {
        mainContainer.isVisible = false
        mainContainer.isManaged = false
    }

    override fun reset() {}

    companion object {
        const val NO_ID = -1L
        const val CONTAINER_WIDTH = 120.0
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}