package lt.markmerkk.widgets

import com.jfoenix.controls.JFXSpinner
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.web.WebView
import lt.markmerkk.Styles
import lt.markmerkk.showIf
import lt.markmerkk.ui_2.views.jfxSpinner
import org.slf4j.LoggerFactory
import rx.Subscription
import rx.observables.JavaFxObservable
import tornadofx.*

@Deprecated("Webview is very costly, don't use it")
class HelpWidget: Fragment() {

    private lateinit var viewWebview: WebView
    private lateinit var viewProgress: JFXSpinner

    private var subsProgress: Subscription? = null

    override val root: Parent = borderpane {
        minWidth = 650.0
        minHeight = 450.0
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Help wiki") {
                    addClass(Styles.dialogHeader)
                }
                viewProgress = jfxSpinner {
                    style {
                        padding = box(all = 4.px)
                    }
                    val boxDimen = 42.0
                    minWidth = boxDimen
                    maxWidth = boxDimen
                    minHeight = boxDimen
                    maxHeight = boxDimen
                    hide()
                }
            }
        }
        center {
            viewWebview = webview {
                engine.load("https://github.com/marius-m/wt4/wiki#wt4")
            }
        }
    }

    override fun onDock() {
        super.onDock()
        subsProgress = JavaFxObservable.valuesOf(viewWebview.engine.loadWorker.runningProperty())
                .subscribe({ viewProgress.showIf(it) }, { error -> logger.warn("JFX prop error", error) })
    }

    override fun onUndock() {
        subsProgress?.unsubscribe()
        super.onUndock()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HelpWidget::class.java)!!
    }

}