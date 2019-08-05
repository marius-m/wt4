package lt.markmerkk.widgets

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.interactors.ClockRunBridgeImpl
import lt.markmerkk.ui_2.bridges.UIEButtonClock
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.hourglass.HourGlass
import tornadofx.*
import javax.inject.Inject

class ClockWidget: View() {

    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: EventBus

    private lateinit var jfxButtonClock: JFXButton
    private lateinit var jfxButtonClockSettings: JFXButton

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        jfxButtonClock = jfxButton {
            addClass(Styles.buttonMenu)
            graphic = graphics.from(Glyph.CLOCK, Color.WHITE, 24.0)
        }
        vbox(alignment = Pos.TOP_RIGHT) {
            isPickOnBounds = false
            jfxButtonClockSettings = jfxButton {
                addClass(Styles.buttonMenuMini)
                graphic = graphics.from(Glyph.INSERT, Color.WHITE, 10.0)
            }
        }
    }

    private val buttonClockListener = object : UIEButtonClock.Listener {

        override fun onClickClock(isSelected: Boolean) {
//            clockRunBridge.setRunning(isSelected)
        }

        override fun onClickClockSettings() {
            eventBus.post(EventInflateDialog(DialogType.ACTIVE_CLOCK))
        }

    }

    override fun onDock() {
        super.onDock()
        val uieButtonClock = UIEButtonClock(
                graphics,
                buttonClockListener,
                jfxButtonClock,
                jfxButtonClockSettings
        )
        val clockRunBridge = ClockRunBridgeImpl(
                uieButtonClock,
                hourGlass,
                logStorage
        )
    }



}