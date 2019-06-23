package lt.markmerkk.ui_2.bridges

import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.ui_2.views.calendar_edit.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.hgrow
import tornadofx.vgrow


class UIEButtonCalendarQuickEdit(
        private val node: ExternalSourceNode<StackPane>,
        private val strings: Strings,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider,
        private val graphics: Graphics<SVGGlyph>,
        private val logChangeValidator: LogChangeValidator
) : UIElement<Node>, QuickEditActionChangeListener {

    private val mainContainer = VBox()
    private val timeChangeValidator = TimeChangeValidator
    private val quickEditActions = setOf(
            QuickEditAction.MOVE,
            QuickEditAction.SCALE,
            QuickEditAction.SCALE10x
    )
    private val uiPrefs = QuickEditUiPrefs(
            prefHeightContainer = 32.0,
            prefWidthTypeSelector = 120.0,
            prefWidthActionIcons = 30.0,
            maxWidthContainer = 240.0, // 4 * actionIcons + typeSelector
            widthActionIcon = 8.0,
            heightActionIcon = 8.0,
            widthActionIconFaster = 14.0,
            heightActionIconFaster = 10.0
    )
    var selectLogId: Long = NO_ID
        private set

    private val listenerViewScale = object : QuickEditWidgetScale.Listener {
        override fun shrinkFromStart(minutes: Int) {
            val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
            val newTimeGap = timeChangeValidator.shrinkFromStart(
                    TimeGap.from(
                            timeProvider.roundDateTime(simpleLog.start),
                            timeProvider.roundDateTime(simpleLog.end)
                    ),
                    minutes
            )
            updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        }

        override fun expandToStart(minutes: Int) {
            val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
            val newTimeGap = timeChangeValidator.expandToStart(
                    TimeGap.from(
                            timeProvider.roundDateTime(simpleLog.start),
                            timeProvider.roundDateTime(simpleLog.end)
                    ),
                    minutes
            )
            updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        }

        override fun shrinkFromEnd(minutes: Int) {
            val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
            val newTimeGap = timeChangeValidator.shrinkFromEnd(
                    TimeGap.from(
                            timeProvider.roundDateTime(simpleLog.start),
                            timeProvider.roundDateTime(simpleLog.end)
                    ),
                    minutes
            )
            updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        }

        override fun expandToEnd(minutes: Int) {
            val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
            val newTimeGap = timeChangeValidator.expandToEnd(
                    TimeGap.from(
                            timeProvider.roundDateTime(simpleLog.start),
                            timeProvider.roundDateTime(simpleLog.end)
                    ),
                    minutes
            )
            updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        }

    }
    private val viewQuickEditWidgetScale = QuickEditWidgetScale(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            uiPrefs = uiPrefs,
            graphics = graphics,
            scaleStepMinutes = 1,
            listener = listenerViewScale
    )

    private val viewQuickEditWidgetScale10 = QuickEditWidgetScale(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            uiPrefs = uiPrefs,
            graphics = graphics,
            scaleStepMinutes = 10,
            listener = listenerViewScale
    )

    private val viewQuickEditWidgetMove = QuickEditWidgetMove(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            uiPrefs = uiPrefs,
            graphics = graphics,
            listener = object : QuickEditWidgetMove.Listener {
                override fun moveForward(minutes: Int) {
                    val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
                    val newTimeGap = timeChangeValidator.moveForward(
                            TimeGap.from(
                                    timeProvider.roundDateTime(simpleLog.start),
                                    timeProvider.roundDateTime(simpleLog.end)
                            ),
                            minutes
                    )
                    updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
                }

                override fun moveBackward(minutes: Int) {
                    val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
                    val newTimeGap = timeChangeValidator.moveBackward(
                            TimeGap.from(
                                    timeProvider.roundDateTime(simpleLog.start),
                                    timeProvider.roundDateTime(simpleLog.end)
                            ),
                            minutes
                    )
                    updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
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
            QuickEditAction.SCALE to viewQuickEditWidgetScale,
            QuickEditAction.SCALE10x to viewQuickEditWidgetScale10
    )

    init {
        viewsQuickEditWidgets.values.forEach { mainContainer.children.add(it.root) }
        mainContainer.maxWidth = uiPrefs.prefHeightContainer
        mainContainer.maxHeight = uiPrefs.prefHeightContainer
        mainContainer.vgrow = Priority.NEVER
        mainContainer.hgrow = Priority.NEVER
        node.rootNode().children.add(mainContainer)
        StackPane.setAlignment(mainContainer, Pos.TOP_RIGHT)
        StackPane.setMargin(mainContainer, Insets(10.0, 20.0, 0.0, 0.0))
        onActiveActionChange(QuickEditAction.MOVE)
    }

    fun changeLogSelection(id: Long) {
        this.selectLogId = id
        if (logChangeValidator.canEditSimpleLog(selectLogId)) {
            show()
        } else {
            hide()
        }
    }

    fun changeLogSelectionToNoSelection() {
        this.selectLogId = NO_ID
        hide()
    }

    private fun updateLog(
            oldLog: SimpleLog,
            start: DateTime,
            end: DateTime
    ) {
        val newSimpleLog = SimpleLogBuilder(oldLog)
                .setStart(start.millis)
                .setEnd(end.millis)
                .build()
        if (logChangeValidator.canEditSimpleLog(selectLogId)) {
            logStorage.update(newSimpleLog)
        }
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
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}