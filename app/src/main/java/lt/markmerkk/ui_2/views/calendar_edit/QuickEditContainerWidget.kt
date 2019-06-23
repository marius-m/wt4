package lt.markmerkk.ui_2.views.calendar_edit

import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.layout.Priority
import lt.markmerkk.Const
import lt.markmerkk.Graphics
import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.ui_2.views.LifecycleView
import lt.markmerkk.ui_2.views.SelectableView
import lt.markmerkk.ui_2.views.VisibilityChangeableView
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import tornadofx.*

class QuickEditContainerWidget(
        private val presenter: QuickEditContract.ContainerPresenter,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider,
        private val graphics: Graphics<SVGGlyph>,
        private val logChangeValidator: LogChangeValidator
) : View(),
        QuickEditContract.ContainerView,
        QuickEditActionChangeListener,
        LifecycleView,
        VisibilityChangeableView {

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

    private val widgetMap = mapOf<QuickEditAction, View>(
            QuickEditAction.MOVE to QuickEditWidgetMove(
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
            ),
            QuickEditAction.SCALE to QuickEditWidgetScale(
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
            ),
            QuickEditAction.SCALE10x to QuickEditWidgetScale(
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
    )

    override val root: Parent = vbox {
        vgrow = Priority.NEVER
        hgrow = Priority.NEVER
        maxWidth = uiPrefs.prefHeightContainer
        maxHeight = uiPrefs.prefHeightContainer
        widgetMap.forEach { children.add(it.value.root) }
    }

    private var selectedLogId: Long = Const.NO_ID

    init {
        onActiveActionChange(QuickEditAction.MOVE)
    }

    override fun onAttach() {
        presenter.onAttach(this)
        widgetMap.values
                .filterIsInstance<LifecycleView>()
                .forEach { it.onAttach() }
        changeToNoSelection()
    }

    override fun onDetach() {
        presenter.onDetach()
        widgetMap.values
                .filterIsInstance<LifecycleView>()
                .forEach { it.onDetach() }
    }

    override fun changeLogSelection(selectId: Long) {
        this.selectedLogId = selectId
        widgetMap.values
                .filterIsInstance<SelectableView>()
                .forEach { it.onSelectLog(selectId) }
        changeVisibility(logChangeValidator.canEditSimpleLog(selectId))
    }

    override fun changeToNoSelection() {
        this.selectedLogId = Const.NO_ID
        widgetMap.values
                .filterIsInstance<SelectableView>()
                .forEach { it.onSelectLog(Const.NO_ID) }
        changeVisibility(isVisible = false)
    }

    override fun selectedId(): Long = selectedLogId

    override fun changeVisibility(isVisible: Boolean) {
        root.isVisible = isVisible
        root.isManaged = isVisible
    }

    override fun onActiveActionChange(quickEditAction: QuickEditAction) {
        widgetMap.values
                .filterIsInstance<QuickEditChangableAction>()
                .forEach { it.changeActiveAction(quickEditAction) }
        widgetMap.getValue(quickEditAction)
                .let { (it as VisibilityChangeableView).changeVisibility(isVisible = true) }
        widgetMap
                .filter { it.key != quickEditAction }
                .map { it.value }
                .filterIsInstance<VisibilityChangeableView>()
                .forEach { it.changeVisibility(isVisible = false) }
    }
}