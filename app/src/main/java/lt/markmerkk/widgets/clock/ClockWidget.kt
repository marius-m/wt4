package lt.markmerkk.widgets.clock

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import lt.markmerkk.*
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.events.EventLogDetailsInitActiveClock
import lt.markmerkk.events.EventMainToggleLogDetails
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.UIEUtils
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.widgets.edit.LogDetailsWidget
import tornadofx.*
import javax.inject.Inject

class ClockWidget: Fragment(), ClockContract.View {

    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var resultDispatcher: ResultDispatcher

    private lateinit var jfxButtonClock: JFXButton
    private lateinit var jfxButtonClockSettings: JFXButton

    init {
        Main.component().inject(this)
    }

    private val presenter: ClockContract.Presenter = ClockPresenter(hourGlass, logStorage, timeProvider, eventBus)
    private val glyphClock = graphics.from(Glyph.PLAY, Color.WHITE, 14.0, 16.0)

    override val root: Parent = stackpane {
        jfxButtonClock = jfxButton {
            graphic = glyphClock
            addClass(Styles.buttonMenu)
            action {
                presenter.toggleClock()
            }
        }
        vbox(alignment = Pos.TOP_RIGHT) {
            isPickOnBounds = false
            jfxButtonClockSettings = jfxButton {
                addClass(Styles.buttonMenuMini)
                graphic = graphics.from(Glyph.STOP, Color.WHITE, 8.0)
                action {
                    presenter.cancelClock()
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter.onAttach(this)
        jfxButtonClockSettings.hide()
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
    }

    override fun showActive(timeAsString: String) {
        jfxButtonClock.font = Font.font(UIEUtils.fontSizeBasedOnLength(timeAsString))
        jfxButtonClock.text = timeAsString
        jfxButtonClock.graphic = null
        jfxButtonClockSettings.show()
    }

    override fun showInactive() {
        jfxButtonClock.graphic = glyphClock
        jfxButtonClock.text = ""
        jfxButtonClockSettings.hide()
    }


}