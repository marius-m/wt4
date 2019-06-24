package lt.markmerkk.ui_2.views.ticket_split

import com.jfoenix.controls.JFXDialog
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Strings
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSlider
import lt.markmerkk.ui_2.views.jfxTextArea
import lt.markmerkk.ui_2.views.jfxTextField
import tornadofx.*

class TicketSplitWidget(
        private val strings: Strings,
        private val graphics: Graphics<SVGGlyph>,
        private val jfxDialog: JFXDialog
) : View() {

    private lateinit var viewDateTimeFrom: Label
    private lateinit var viewDateTimeMiddle: Label
    private lateinit var viewDateTimeTo: Label
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
            left {
                viewDateTimeFrom = label("2018-05-20 14:30")
            }
            center {
                viewDateTimeMiddle = label("14:30")
            }
            right {
                viewDateTimeTo = label("2018-05-20 14:30")
            }
        }
        jfxSlider {
            orientation = Orientation.HORIZONTAL
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
                promptText = "Ticket"
                isEditable = false
                text = "DEV-12334"
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
                text = "Code review"
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
                promptText = "Original comment"
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
                promptText = "New comment"
            }
        }
    }
}