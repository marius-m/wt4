package lt.markmerkk.widgets.dialogs

import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Strings
import lt.markmerkk.Styles
import lt.markmerkk.Tags
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.Logs.withLogInstance
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class DialogConfirmWidget : Fragment() {

    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    init {
        Main.component().inject(this)
    }

    private val dialogBundle: DialogBundle = resultDispatcher
        .consume(RESULT_DISPATCH_KEY_BUNDLE, DialogBundle::class.java) ?: DialogBundle.asEmpty()

    override val root: Parent = borderpane {
        val actionConfirm = {
            dialogBundle.onConfirm.invoke()
            close()
        }
        shortcut(KeyCombination.valueOf("Esc")) { close() }
        shortcut(KeyCombination.valueOf("Meta+Enter"), actionConfirm)
        shortcut(KeyCombination.valueOf("Ctrl+Enter"), actionConfirm)
        addClass(Styles.dialogAlertContainer)
        top {
            hbox(spacing = 4, alignment = Pos.CENTER_LEFT) {
                label(dialogBundle.header) {
                    HBox.setHgrow(this, Priority.ALWAYS)
                    addClass(Styles.dialogAlertTextH1)
                    maxWidth = Double.MAX_VALUE
                }
                val imageAlert = graphics.from(
                    glyph = Glyph.WARNING,
                    color = Color.BLACK,
                    width = 24.0,
                    height = 20.0,
                )
                add(imageAlert)
                HBox.setHgrow(imageAlert, Priority.NEVER)
            }
        }
        center {
            vbox {
                label(dialogBundle.content) {
                    addClass(Styles.dialogAlertContentContainer)
                    addClass(Styles.dialogAlertTextRegular)
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton(strings.getString("generic_dialog_yes").uppercase()) {
                    setOnAction {
                        dialogBundle.onConfirm.invoke()
                        close()
                    }
                }
                jfxButton(strings.getString("generic_dialog_no").uppercase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        l.debug("onDock(dialogBundle: {})".withLogInstance(this), dialogBundle)
    }

    override fun onUndock() {
        l.debug("onUndock()".withLogInstance(this))
        super.onUndock()
    }

    class DialogBundle(
        val header: String,
        val content: String,
        val onConfirm: () -> Unit,
    ) {
        companion object {
            fun asEmpty(): DialogBundle {
                return DialogBundle(
                    header = "",
                    content = "",
                    onConfirm = {},
                )
            }
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(Tags.INTERNAL)!!
        const val RESULT_DISPATCH_KEY_BUNDLE = "Kb3t*Xy49M1iWS&d"
    }
}