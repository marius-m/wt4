package lt.markmerkk.ui_2.views.calendar_edit

import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.paint.Paint
import lt.markmerkk.*
import lt.markmerkk.ui_2.views.LifecycleView
import lt.markmerkk.ui_2.views.SelectableView
import lt.markmerkk.ui_2.views.VisibilityChangeableView
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import tornadofx.*
import javax.inject.Inject

class QuickEditContainerWidget: Fragment(),
        QuickEditContract.ContainerView,
        QuickEditActionChangeListener,
        VisibilityChangeableView
{

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var logChangeValidator: LogChangeValidator

    init {
        Main.component().inject(this)
    }

    private lateinit var presenter: QuickEditContract.ContainerPresenter

    private val timeChangeValidator = TimeChangeValidator
    private val quickEditActions = setOf(
            QuickEditAction.MOVE,
            QuickEditAction.SCALE,
            QuickEditAction.SCALE10x
    )
    private val uiPrefs = QuickEditUiPrefs(
            prefHeightContainer = 28.0,
            prefWidthTypeSelector = 120.0,
            prefWidthActionIcons = 28.0,
            maxWidthContainer = 240.0, // 4 * actionIcons + typeSelector
            widthActionIcon = 8.0,
            heightActionIcon = 8.0,
            widthActionIconFaster = 14.0,
            heightActionIconFaster = 10.0
    )


    private val selectEntryProvider = object : QuickEditContract.SelectEntryProvider {
        override fun suggestNewEntry(newEntryId: Long) {
            selectedLogId = newEntryId
        }
        override fun entryId(): Long = selectedLogId
    }
    private val widgetMap = mapOf<QuickEditAction, Fragment>(
            QuickEditAction.MOVE to QuickEditWidgetMove(
                    quickEditActions = quickEditActions,
                    quickEditActionChangeListener = this,
                    uiPrefs = uiPrefs,
                    graphics = graphics,
                    presenter = QuickEditPresenterMove(
                            logStorage,
                            timeChangeValidator,
                            timeProvider,
                            logChangeValidator,
                            selectEntryProvider
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
                            logChangeValidator,
                            selectEntryProvider
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
                            logChangeValidator,
                            selectEntryProvider
                    )
            )
    )

    override val root: Parent = vbox {
        vgrow = Priority.NEVER
        hgrow = Priority.NEVER
        maxWidth = uiPrefs.prefHeightContainer
        maxHeight = uiPrefs.prefHeightContainer
        widgetMap.forEach { children.add(it.value.root) }
        style {
            backgroundColor.add(Paint.valueOf(MaterialColors.LIGHTEST))
            backgroundRadius.add(box(20.px))
        }
    }

    private var selectedLogId: Long = Const.NO_ID

    override fun onDock() {
        super.onDock()
        presenter = QuickEditContainerPresenter(eventBus)
        presenter.onAttach(this)
        changeToNoSelection()

        onActiveActionChange(QuickEditAction.MOVE)
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
    }

    override fun changeLogSelection(selectId: Long) {
        this.selectedLogId = selectId
        changeVisibility(logChangeValidator.canEditSimpleLog(selectId))
    }

    override fun changeToNoSelection() {
        this.selectedLogId = Const.NO_ID
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