package lt.markmerkk.widgets.edit

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXTimePicker
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.ui_2.views.*
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import javax.inject.Inject

class LogDetailsWidget: View() {

    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    private lateinit var viewDatePickerFrom: JFXDatePicker
    private lateinit var viewTimePickerFrom: JFXTimePicker
    private lateinit var viewButtonSubtractFrom: JFXButton
    private lateinit var viewButtonAppendFrom: JFXButton
    private lateinit var viewDatePickerTo: JFXDatePicker
    private lateinit var viewTimePickerTo: JFXTimePicker
    private lateinit var viewButtonSubtractTo: JFXButton
    private lateinit var viewButtonAppendTo: JFXButton
    private lateinit var viewTextFieldTicket: JFXTextField
    private lateinit var viewTextTicketDesc: Label
    private lateinit var viewButtonTicketLink: JFXButton
    private lateinit var viewButtonSearch: JFXButton

    init {
        Main.component().inject(this)
    }

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            label("Log details") {
                addClass(Styles.dialogHeader)
            }
        }
        center {
            vbox {
                label("From") {
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
                hbox(spacing = 4) {
                    viewDatePickerFrom = jfxDatePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewTimePickerFrom = jfxTimePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewButtonSubtractFrom = jfxButton("-") {
                        isFocusTraversable = false
                    }
                    viewButtonAppendFrom = jfxButton("+") {
                        isFocusTraversable = false
                    }
                }
                label("To") {
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
                hbox(spacing = 4) {
                    viewDatePickerTo = jfxDatePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewTimePickerTo = jfxTimePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewButtonSubtractTo = jfxButton("-") {
                        isFocusTraversable = false
                    }
                    viewButtonAppendTo = jfxButton("+") {
                        isFocusTraversable = false
                    }
                }
                hbox(spacing = 4) {
                    style {
                        padding = box(
                                top = 20.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                    viewTextFieldTicket = jfxTextField {
                        minWidth = 120.0
                        maxWidth = 120.0
                        focusColor = Styles.cActiveRed
                        isLabelFloat = true
                        promptText = "Ticket ID"
                        unFocusColor = Color.BLACK
                    }
                    viewTextTicketDesc = label {  }
                    viewButtonTicketLink = jfxButton {
                        isFocusTraversable = false
                        graphic = graphics.from(Glyph.LINK, Color.BLACK, 16.0, 20.0)
                    }
                    viewButtonSearch = jfxButton {
                        isFocusTraversable = false
                        graphic = graphics.from(Glyph.SEARCH, Color.BLACK, 20.0)
                    }
                }
                hbox {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    style {
                        padding = box(
                                top = 20.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                    jfxTextArea {
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                        focusColor = Styles.cActiveRed
                        isLabelFloat = true
                        promptText = "Comment"
                        prefRowCount = 5
                    }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Dismiss") {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }
}