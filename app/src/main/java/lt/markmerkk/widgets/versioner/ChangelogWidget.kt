package lt.markmerkk.widgets.versioner

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.web.WebView
import lt.markmerkk.BuildConfig
import lt.markmerkk.Styles
import lt.markmerkk.Tags
import lt.markmerkk.versioner.Changelog
import org.slf4j.LoggerFactory
import tornadofx.*

class ChangelogWidget : Fragment() {

    private lateinit var viewWebview: WebView
    private lateinit var viewLabelLocal: Label
    private lateinit var viewLabelRemote: Label

    override val root: Parent = borderpane {
        minWidth = 650.0
        minHeight = 450.0
        addClass(Styles.dialogContainer)
        top {
        }
        top {
            vbox(spacing = 4) {
                style {
                    padding = box(
                            top = 0.px,
                            left = 0.px,
                            right = 0.px,
                            bottom = 10.px
                    )
                }
                label("What's new?") {
                    addClass(Styles.dialogHeader)
                }
                val localVersion = Changelog.versionFrom(BuildConfig.versionName)
                viewLabelLocal = label("Current version: $localVersion") {  }
                viewLabelRemote = label("Version available: ")
            }
        }
        center {
            viewWebview = webview { }
        }
    }

    fun render(changelog: Changelog) {
        val options = MutableDataSet()
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()
        val document = parser.parse(changelog.contentAsString)
        val changelogHtml = renderer.render(document)
        viewWebview.engine.loadContent(changelogHtml)
        viewLabelRemote.text = "Version available: ${changelog.version}"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}