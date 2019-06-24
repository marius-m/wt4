package lt.markmerkk.ui_2.views.ticket_split

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXSlider
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tornadofx.*

class TicketSplitWidget(
        private val strings: Strings,
        private val graphics: Graphics<SVGGlyph>,
        private val jfxDialog: JFXDialog,
        private val presenter: TicketSplitContract.Presenter
) : View(),
        TicketSplitContract.View,
        LifecycleView
{
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
    val header: Parent = vbox {
        label(strings.getString("ticket_split_header_title")) {
            addClass("dialog-header")
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
            jfxDialog.close()
        }
    }

    val actions: List<Parent> = listOf(
            actionSplit,
            jfxButton(strings.getString("general_dismiss").toUpperCase()) {
                setOnAction { jfxDialog.close() }
            }
    )

    override val root: Parent = vbox {
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
        viewLabelError = label {
            addClass("mini-label")
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

    private val viewSliderChangeListener = ChangeListener<Number> { observable, from, to ->
        presenter.changeSplitBalance(balancePercent = to.toInt())
    }

    override fun onAttach() {
        presenter.onAttach(this)
        viewSlider.valueProperty().addListener(viewSliderChangeListener)
    }

    override fun onDetach() {
        viewSlider.valueProperty().removeListener(viewSliderChangeListener)
        presenter.onDetach()
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