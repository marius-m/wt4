package lt.markmerkk.ui_2.views.ticket_split

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXSlider
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Strings
import lt.markmerkk.Tags
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
    val header: Parent = vbox {
        label(strings.getString("ticket_split_header_title")) {
            addClass("dialog-header")
        }
    }

    val actions: List<Parent> = listOf(
            jfxButton(strings.getString("general_split").toUpperCase()) {
                graphic = graphics.from(Glyph.SPLIT, Color.BLACK, size = 12.0)
            },
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
        hbox {
            style {
                padding = box(
                        top = 20.px,
                        left = 0.px,
                        right = 0.px,
                        bottom = 0.px
                )
            }
            jfxTextField {
                hgrow = Priority.NEVER
                vgrow = Priority.NEVER
                isLabelFloat = true
                promptText = strings.getString("ticket_split_label_ticket")
                isEditable = false
                text = ""
                prefWidth = 80.0
            }
            label {
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
            jfxTextArea {
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
            jfxTextArea {
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

    override fun onSplitTimeUpdate(
            start: DateTime,
            end: DateTime,
            splitGap: DateTime
    ) {
        viewDateTimeFrom.text = LogFormatters.longFormat.print(start)
        viewDateTimeTo.text = LogFormatters.longFormat.print(end)
        viewDateTimeMiddle.text = LogFormatters.shortFormat.print(splitGap)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKET_SPLIT)
        const val RESULT_DISPATCH_KEY_ENTITY = "EIE5nv2wNk"
    }

}