package lt.markmerkk.widgets

import com.airhacks.afterburner.views.FXMLView
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.*
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.day.DayView
import lt.markmerkk.ui.display.DisplayLogView
import lt.markmerkk.ui.graphs.GraphsFxView
import lt.markmerkk.ui.week2.WeekView2
import lt.markmerkk.ui_2.*
import lt.markmerkk.ui_2.bridges.UIEButtonDisplayView
import lt.markmerkk.ui_2.bridges.UIEButtonSettings
import lt.markmerkk.ui_2.bridges.UIECenterView
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerPresenter
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidgetPresenterDefault
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.progress.ProgressWidget
import lt.markmerkk.ui_2.views.progress.ProgressWidgetPresenter
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.widgets.clock.ClockWidget
import lt.markmerkk.widgets.edit.LogDetailsWidget
import lt.markmerkk.widgets.tickets.TicketWidget
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class MainWidget : View(), ExternalSourceNode<StackPane> {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var strings: Strings
    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var eventBus: com.google.common.eventbus.EventBus
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logChangeValidator: LogChangeValidator
    @Inject lateinit var syncInteractor: SyncInteractor

    lateinit var jfxButtonDisplayView: JFXButton
    lateinit var jfxButtonSettings: JFXButton
    lateinit var jfxContainerContent: BorderPane
    lateinit var jfxContainerContentLeft: HBox
    lateinit var jfxContainerContentRight: HBox

    lateinit var uieButtonDisplayView: UIEButtonDisplayView
    lateinit var uieButtonSettings: UIEButtonSettings
    lateinit var uieCenterView: UIECenterView
    lateinit var snackBar: JFXSnackbar
    lateinit var widgetDateChange: QuickDateChangeWidget
    lateinit var widgetProgress: ProgressWidget
    lateinit var widgetLogQuickEdit: QuickEditContainerWidget

    var currentDisplayType = DisplayType.CALENDAR_VIEW_DAY

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        borderpane {
            left {
                vbox(spacing = 4) {
                    style {
                        backgroundColor.add(Styles.cBackgroundPrimary)
                        padding = box(vertical = 4.px, horizontal = 10.px)
                    }
                    add(find<ClockWidget>().root)
                    vbox(spacing = 4, alignment = Pos.BOTTOM_CENTER) {
                        vgrow = Priority.ALWAYS
                        jfxButtonDisplayView = jfxButton {
                            addClass(Styles.buttonMenu)
                        }
                        jfxButtonSettings = jfxButton {
                            addClass(Styles.buttonMenu)
                            graphic = graphics.from(Glyph.SETTINGS, Color.BLACK, 24.0)
                        }
                    }
                }
            }
            center {
                jfxContainerContent = borderpane {
                    top {
                        borderpane {
                            left {
                                jfxContainerContentLeft = hbox { }
                            }
                            right {
                                jfxContainerContentRight = hbox { }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()

        // Init ui elements
        widgetDateChange = QuickDateChangeWidget(
                graphics,
                QuickDateChangeWidgetPresenterDefault(
                        this,
                        logStorage
                )
        )
        widgetProgress = ProgressWidget(
                presenter = ProgressWidgetPresenter(syncInteractor),
                graphics = graphics
        )
        widgetLogQuickEdit = QuickEditContainerWidget(
                presenter = QuickEditContainerPresenter(eventBus),
                logStorage = logStorage,
                timeProvider = timeProvider,
                graphics = graphics,
                logChangeValidator = logChangeValidator
        )
        snackBar = JFXSnackbar(root as StackPane)
        uieButtonSettings = UIEButtonSettings(graphics, strings, this, jfxButtonSettings, eventBus)
        uieButtonDisplayView = UIEButtonDisplayView(graphics, this, jfxButtonDisplayView, buttonChangeDisplayViewExternalListener)
        uieCenterView = UIECenterView(jfxContainerContent)
        jfxContainerContentRight.children.add(widgetProgress.root)
        jfxContainerContentLeft.children.add(widgetDateChange.root)
        jfxContainerContentLeft.children.add(widgetLogQuickEdit.root)
        changeDisplayByDisplayType(currentDisplayType)

        // Init interactors
        eventBus.register(this)
        widgetDateChange.onAttach()
        widgetProgress.onAttach()
        widgetLogQuickEdit.onAttach()
    }

    override fun onUndock() {
        widgetLogQuickEdit.onDetach()
        widgetProgress.onDetach()
        widgetDateChange.onDetach()
        eventBus.unregister(this)
        if (hourGlass.state == HourGlass.State.RUNNING) {
            hourGlass.stop()
        }
    }

    //region Events

    // todo : function will probably will have to be extracted to somewhere else
    @Subscribe
    fun onDisplayTypeChange(eventChangeDisplayType: EventChangeDisplayType) {
        changeDisplayByDisplayType(eventChangeDisplayType.displayType)
    }

    @Subscribe
    fun onSnackBarMessage(event: EventSnackBarMessage) {
        val label = Label(event.message)
                .apply {
                    val paddingHorizontal = 10.0
                    val paddingVertical = 8.0
                    padding = Insets(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal)
                    textFill = Paint.valueOf("white")
                    maxWidth = stageProperties.width
                }
        val hBox = HBox(10.0, label)
        snackBar.enqueue(JFXSnackbar.SnackbarEvent(hBox))
    }

    @Subscribe
    fun onEventEditLog(event: EventEditLog) {
        if (event.logs.isEmpty()) {
            logger.warn("No items are selected. Have you bound selected items ?")
            return
        }
        when (event.editType) {
            LogEditType.UPDATE -> {
                resultDispatcher.publish(LogDetailsWidget.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventInflateDialog(DialogType.LOG_EDIT))
            }
            LogEditType.DELETE -> logStorage.delete(event.logs.first())
            LogEditType.CLONE -> {
                val logToClone = event.logs.first()
                val newLog = SimpleLogBuilder()
                        .setStart(logToClone.start)
                        .setEnd(logToClone.end)
                        .setTask(logToClone.task)
                        .setComment(logToClone.comment)
                        .build()
                logStorage.insert(newLog)
            }
            LogEditType.SPLIT -> {
                resultDispatcher.publish(TicketSplitController.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventInflateDialog(DialogType.TICKET_SPLIT))
            }
            LogEditType.MERGE -> {
                resultDispatcher.publish(TicketMergeController.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventInflateDialog(DialogType.TICKET_MERGE))
            }
        }.javaClass
    }

    //endregion

    //region Convenience

    fun changeDisplayByDisplayType(displayType: DisplayType) {
        val oldView = uieCenterView.raw()
        when (displayType) {
            DisplayType.TABLE_VIEW_DETAIL -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(DisplayLogView())
            }
            DisplayType.CALENDAR_VIEW_DAY -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(DayView())
            }
            DisplayType.CALENDAR_VIEW_WEEK -> {
                logStorage.displayType = DisplayTypeLength.WEEK
                uieCenterView.populate(WeekView2())
            }
            DisplayType.GRAPHS -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(GraphsFxView())
            }
            else -> throw IllegalStateException("Display cannot be handled")
        }
        InjectorNoDI.forget(oldView)
        currentDisplayType = displayType
    }

    //endregion

    override fun rootNode(): StackPane = root as StackPane

    //region Listeners

    private val buttonChangeDisplayViewExternalListener = object : UIEButtonDisplayView.ExternalListener {
        override fun currentDisplayType(): DisplayType {
            return currentDisplayType
        }
    }

    //endregion

    @Subscribe
    fun eventInflateDialog(event: EventInflateDialog) {
        when (event.type) {
            DialogType.ACTIVE_CLOCK -> {
                find<LogDetailsWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
            }
            DialogType.LOG_EDIT -> {
                find<LogDetailsWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
            }
            DialogType.TICKET_SEARCH -> {
                find<TicketWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
            }
            DialogType.TICKET_SPLIT -> openDialog(TicketSplitDialog())
            DialogType.TICKET_MERGE -> openDialog(TicketMergeDialog())
        }
    }

    private fun openDialog(dialog: FXMLView) {
        val jfxDialog = dialog.view as JFXDialog
        jfxDialog.show(root as StackPane)
        jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
    }

    fun showInfo(message: String) {
        information(
                header = "Info",
                content = message
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainWidget::class.java)!!
    }

}