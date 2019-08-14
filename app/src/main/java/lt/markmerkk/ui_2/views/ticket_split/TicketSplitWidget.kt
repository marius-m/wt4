package lt.markmerkk.ui_2.views.ticket_split

import com.jfoenix.controls.JFXSlider
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.LogSplitter
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class TicketSplitWidget : View(), TicketSplitContract.View {

    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var schedulerProvider: SchedulerProvider

    private lateinit var viewDateTimeFrom: Label
    private lateinit var viewDateTimeMiddle: Label
    private lateinit var viewDateTimeTo: Label
    private lateinit var viewSlider: JFXSlider
    private lateinit var viewContainerTicket: HBox
    private lateinit var viewTextTicketCode: JFXTextField
    private lateinit var viewTextTicketTitle: Label
    private lateinit var viewTextCommentOriginal: JFXTextArea
    private lateinit var viewTextCommentNew: JFXTextArea
    private lateinit var viewLabelError: Label

    init {
        Main.component().inject(this)
    }

    private lateinit var presenter: TicketSplitContract.Presenter

    val header: Parent = vbox {
        label(strings.getString("ticket_split_header_title")) {
            addClass(Styles.dialogHeader)
        }
    }

    private val actionSplit = jfxButton(strings.getString("general_split").toUpperCase()) {
        graphic = graphics.from(Glyph.SPLIT, Color.BLACK, size = 12.0)
        setOnAction {
            presenter.split(
                    ticketName = viewTextTicketCode.text,
                    originalComment = viewTextCommentOriginal.text,
                    newComment = viewTextCommentNew.text
            )
            close()
        }
    }

    val actions: List<Parent> = listOf(
            actionSplit,
            jfxButton(strings.getString("general_dismiss").toUpperCase()) {
                action { close() }
            }
    )

    override val root: Parent = stackpane {
        setOnKeyPressed { keyEvent ->
            when {
                (keyEvent.code == KeyCode.ENTER && keyEvent.isMetaDown)
                        || (keyEvent.code == KeyCode.ENTER && keyEvent.isControlDown) -> {
                    presenter.split(
                            ticketName = viewTextTicketCode.text,
                            originalComment = viewTextCommentOriginal.text,
                            newComment = viewTextCommentNew.text
                    )
                    close()
                }
            }
        }
        borderpane {
            addClass(Styles.dialogContainer)
            top {
                label("Ticket slicer") {
                    addClass(Styles.dialogHeader)
                }
            }
            center {
                vbox {
                    borderpane {
                        left { viewDateTimeFrom = label() }
                        center { viewDateTimeMiddle = label() }
                        right { viewDateTimeTo = label() }
                    }
                    viewSlider = jfxSlider {
                        orientation = Orientation.HORIZONTAL
                        min = 1.0
                        max = 100.0
                    }
                    viewContainerTicket = hbox {
                        style {
                            padding = box(
                                    top = 20.px,
                                    left = 0.px,
                                    right = 0.px,
                                    bottom = 0.px
                            )
                        }
                        viewTextTicketCode = jfxTextField {
                            hgrow = Priority.NEVER
                            vgrow = Priority.NEVER
                            isLabelFloat = true
                            promptText = strings.getString("ticket_split_label_ticket")
                            isEditable = false
                            text = ""
                            prefWidth = 80.0
                        }
                        viewTextTicketTitle = label {
                            addClass(Styles.labelRegular)
                            style {
                                padding = box(
                                        top = 4.px,
                                        left = 6.px,
                                        right = 0.px,
                                        bottom = 0.px
                                )
                            }
                            text = ""
                            hgrow = Priority.ALWAYS
                        }
                    }
                    hbox {
                        style {
                            padding = box(
                                    top = 30.px,
                                    left = 0.px,
                                    right = 0.px,
                                    bottom = 0.px
                            )
                        }
                        vgrow = Priority.ALWAYS
                        viewTextCommentOriginal = jfxTextArea {
                            prefWidth = 120.0
                            style {
                                padding = box(
                                        top = 0.px,
                                        left = 0.px,
                                        right = 4.px,
                                        bottom = 0.px
                                )
                            }
                            hgrow = Priority.ALWAYS
                            isLabelFloat = true
                            promptText = strings.getString("ticket_split_label_comment_original")
                        }
                        viewTextCommentNew = jfxTextArea {
                            prefWidth = 120.0
                            style {
                                padding = box(
                                        top = 0.px,
                                        left = 4.px,
                                        right = 0.px,
                                        bottom = 0.px
                                )
                            }
                            hgrow = Priority.ALWAYS
                            isLabelFloat = true
                            promptText = strings.getString("ticket_split_label_comment_new")
                        }
                    }
                    viewLabelError = label("Slice your tickets into two by moving slider for time split and split comments into different columns") {
                        addClass(Styles.labelMini)
                        style {
                            padding = box(
                                    top = 10.px,
                                    left = 0.px,
                                    right = 0.px,
                                    bottom = 0.px
                            )
                        }
                    }
                }
            }
            bottom {
                hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                    addClass(Styles.dialogContainerActionsButtons)
                    jfxButton("Slice".toUpperCase()) {
                        setOnAction {
                            presenter.split(
                                    ticketName = viewTextTicketCode.text,
                                    originalComment = viewTextCommentOriginal.text,
                                    newComment = viewTextCommentNew.text
                            )
                            close()
                        }
                    }
                    jfxButton("Dismiss".toUpperCase()) {
                        setOnAction {
                            close()
                        }
                    }
                }
            }
        }
    }

    private val viewSliderChangeListener = ChangeListener<Number> { _, _, to ->
        presenter.changeSplitBalance(balancePercent = to.toInt())
    }

    override fun onDock() {
        super.onDock()
        viewSlider.value = 50.0
        viewTextCommentOriginal.text = ""
        viewTextCommentNew.text = ""
        presenter = TicketSplitPresenter(
                resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)!!,
                timeProvider,
                logStorage,
                LogSplitter,
                strings,
                ticketsDatabaseRepo,
                schedulerProvider
        )
        presenter.onAttach(this)
        viewSlider.valueProperty().addListener(viewSliderChangeListener)
    }

    override fun onUndock() {
        viewSlider.valueProperty().removeListener(viewSliderChangeListener)
        presenter.onDetach()
        super.onUndock()
    }

    override fun onWorklogInit(
            showTicket: Boolean,
            ticketCode: String,
            originalComment: String,
            isSplitEnabled: Boolean
    ) {
        viewTextTicketCode.text = ticketCode
        viewTextTicketCode.isVisible = showTicket
        viewTextTicketCode.isManaged = showTicket
        viewTextCommentOriginal.text = originalComment
        actionSplit.isDisable = !isSplitEnabled
    }

    override fun onSplitTimeUpdate(
            start: DateTime,
            end: DateTime,
            splitGap: DateTime
    ) {
        viewDateTimeFrom.text = LogFormatters.longFormat.print(start)
        viewDateTimeTo.text = LogFormatters.longFormat.print(end)
        viewDateTimeMiddle.text = LogFormatters.shortFormat.print(splitGap)
    }

    override fun showTicketLabel(ticketTitle: String) {
        viewTextTicketTitle.text = ticketTitle
    }

    override fun showError(error: String) {
        viewLabelError.text = error
        viewLabelError.isVisible = true
        viewLabelError.isManaged = true
    }

    override fun hideError() {
        viewLabelError.text = ""
        viewLabelError.isVisible = false
        viewLabelError.isManaged = false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKET_SPLIT)
        const val RESULT_DISPATCH_KEY_ENTITY = "EIE5nv2wNk"
    }

}