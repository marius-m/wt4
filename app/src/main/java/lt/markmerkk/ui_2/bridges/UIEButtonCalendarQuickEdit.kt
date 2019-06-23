package lt.markmerkk.ui_2.bridges

import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import lt.markmerkk.*
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.views.calendar_edit.*
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import org.slf4j.LoggerFactory
import tornadofx.*


class UIEButtonCalendarQuickEdit(
        private val node: ExternalSourceNode<StackPane>,
        private val strings: Strings,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider,
        private val graphics: Graphics<SVGGlyph>,
        private val logChangeValidator: LogChangeValidator
) : UIElement<Node>, QuickEditActionChangeListener {

    private var selectedLogId: Long = Const.NO_ID
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

    private val viewQuickEditWidgetScale = QuickEditWidgetScale(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            uiPrefs = uiPrefs,
            graphics = graphics,
            scaleStepMinutes = 1,
            presenter = QuickEditPresenterScale(
                    logStorage,
                    timeChangeValidator,
                    timeProvider,
                    logChangeValidator
            )
    )

    private val viewQuickEditWidgetScale10 = QuickEditWidgetScale(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            uiPrefs = uiPrefs,
            graphics = graphics,
            scaleStepMinutes = 10,
            presenter = QuickEditPresenterScale(
                    logStorage,
                    timeChangeValidator,
                    timeProvider,
                    logChangeValidator
            )
    )

    private val viewQuickEditWidgetMove = QuickEditWidgetMove(
            quickEditActions = quickEditActions,
            quickEditActionChangeListener = this,
            uiPrefs = uiPrefs,
            graphics = graphics,
            presenter = QuickEditPresenterMove(
                    logStorage,
                    timeChangeValidator,
                    timeProvider,
                    logChangeValidator
            )
    )

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

    fun onAttach() {
        viewsQuickEditWidgets.values
                .filterIsInstance<QuickEditContract.LifecycleView>()
                .forEach { it.onAttach() }
    }

    fun onDetach() {
        viewsQuickEditWidgets.values
                .filterIsInstance<QuickEditContract.LifecycleView>()
                .forEach { it.onDetach() }
    }

    fun logSelection(): Long = selectedLogId

    fun changeLogSelection(id: Long) {
        this.selectedLogId = id
        viewsQuickEditWidgets.values
                .filterIsInstance<QuickEditContract.SelectableView>()
                .forEach { it.onSelectLog(id) }
        if (logChangeValidator.canEditSimpleLog(id)) {
            show()
        } else {
            hide()
        }
    }

    fun changeLogSelectionToNoSelection() {
        this.selectedLogId = Const.NO_ID
        viewsQuickEditWidgets.values
                .filterIsInstance<QuickEditContract.SelectableView>()
                .forEach { it.onSelectLog(Const.NO_ID) }
        hide()
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
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}