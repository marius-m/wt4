package lt.markmerkk.widgets

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXRadioButton
import javafx.geometry.Pos
import javafx.scene.Parent
import lt.markmerkk.DisplayType
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.events.EventChangeDisplayType
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxRadiobutton
import tornadofx.*
import javax.inject.Inject

class DisplaySelectWidget : View() {

    @Inject lateinit var eventBus: EventBus

    lateinit var viewRadioCalendarDay: JFXRadioButton
    lateinit var viewRadioCalendarWeek: JFXRadioButton
    lateinit var viewRadioList: JFXRadioButton
    lateinit var viewRadios: List<JFXRadioButton>

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        addClass(Styles.dialogContainer)
        borderpane {
            top {
                label("Log display") {
                    addClass(Styles.dialogHeader)
                }
            }
            center {
                vbox(spacing = 8) {
                    viewRadioCalendarDay = jfxRadiobutton("Calendar for day") {
                        action {
                            eventBus.post(EventChangeDisplayType(DisplayType.CALENDAR_VIEW_DAY))
                            close()
                        }
                    }
                    viewRadioCalendarWeek = jfxRadiobutton("Calendar for week") {
                        action {
                            eventBus.post(EventChangeDisplayType(DisplayType.CALENDAR_VIEW_WEEK))
                            close()
                        }
                    }
                    viewRadioList = jfxRadiobutton("List for a day") {
                        action {
                            eventBus.post(EventChangeDisplayType(DisplayType.TABLE_VIEW_DETAIL))
                            close()
                        }
                    }
                }
            }
            bottom {
                hbox(alignment = Pos.CENTER_RIGHT) {
                    addClass(Styles.dialogContainerActionsButtons)
                    jfxButton("Dismiss") {
                        setOnAction { close() }
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        listOf(viewRadioCalendarDay, viewRadioCalendarWeek, viewRadioList)
                .forEach {
                    it.isSelected = false
                }
    }

}