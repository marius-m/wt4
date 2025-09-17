package lt.markmerkk.widgets.export

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Styles
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.ui_2.views.jfxButton
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class ExportSampleWidget: BaseFragment() {

    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var eventBus: WTEventBus

    init {
        Main.component().inject(this)
    }

    private lateinit var inputText: String
    private lateinit var viewText: TextArea

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Export sample") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            vbox(spacing = 4.0) {
                viewText = textarea {
                    vgrow = Priority.ALWAYS
                    prefColumnCount = 10
                    prefRowCount = 6
                    isWrapText = false
                    isEditable = false
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Copy".toUpperCase()) {
                    action {
                        hostServicesInteractor
                                .copyText(inputText)
                        eventBus.post(EventSnackBarMessage("Logs copied :rocket:"))
                    }
                }
                jfxButton("Close".toUpperCase()) {
                    action { close() }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        inputText = resultDispatcher.consume(RESULT_DISPATCH_KEY_SAMPLE, String::class.java) ?: ""
        viewText.text = inputText
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExportSampleWidget::class.java)!!
        const val RESULT_DISPATCH_KEY_SAMPLE = "a7e21c57-a116-48b9-bbc1-836a9fb63b9f"
    }
}