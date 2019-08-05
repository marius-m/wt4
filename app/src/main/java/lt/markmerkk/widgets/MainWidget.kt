package lt.markmerkk.widgets

import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.OverrunStyle
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.interactors.AutoUpdateInteractor
import lt.markmerkk.interactors.KeepAliveGASession
import lt.markmerkk.interactors.KeepAliveInteractor
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui_2.MainView2
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class MainWidget: View() {

    private val graphics: Graphics<SVGGlyph> by di()

    lateinit var jfxButtonDisplay: JFXButton
    lateinit var jfxButtonSettings: JFXButton
    lateinit var jfxContainerContent: VBox
    lateinit var jfxContainerContentLeft: HBox
    lateinit var jfxContainerContentRight: HBox

    override val root: Parent = borderpane {
        left {
            vbox {
                add(ClockWidget().root)
                vbox {
                    jfxButtonDisplay = jfxButton("Display") {
                        addClass("button-default-clock")
                        ellipsisString = "..."
                        textOverrun = OverrunStyle.WORD_ELLIPSIS
                    }
                    jfxButtonSettings = jfxButton("Settings") {
                        addClass("button-default-clock")
                        ellipsisString = "..."
                        textOverrun = OverrunStyle.WORD_ELLIPSIS
                        graphic = graphics.from(Glyph.SETTINGS, Color.BLACK, 24.0)
                    }
                }
            }
        }
        center {
            borderpane {
                top {
                    borderpane {
                        left {
                            jfxContainerContentLeft = hbox {  }
                            jfxContainerContentRight = hbox {  }
                        }
                    }
                }
                center {
                    jfxContainerContent = vbox {  }
                }
            }
        }
    }

    init {
        Main.component().inject(this)
    }

    override fun onDock() {
        super.onDock()
    }

    override fun onUndock() {
        super.onUndock()
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