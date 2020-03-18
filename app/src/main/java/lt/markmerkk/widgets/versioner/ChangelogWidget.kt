package lt.markmerkk.widgets.versioner

import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.web.WebView
import lt.markmerkk.*
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.upgrade.AppDownloader
import lt.markmerkk.versioner.Changelog
import lt.markmerkk.versioner.ChangelogLoader
import lt.markmerkk.widgets.network.Api
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class ChangelogWidget : Fragment() {

    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var api: Api
    @Inject lateinit var schedulerProvider: SchedulerProvider

    init {
        Main.component().inject(this)
    }

    private lateinit var viewArea: TextArea
    private lateinit var viewLabelLocal: Label
    private lateinit var viewLabelRemote: Label

    private lateinit var changelogLoader: ChangelogLoader
    private lateinit var appDownloader: AppDownloader

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
                hyperlink {
                    text = "Download: https://github.com/marius-m/wt4"
                    action {
                        hostServicesInteractor.openLink("https://github.com/marius-m/wt4#downloads")
                    }
                }
                jfxButton("Upgrade") {
                    action {
                        appDownloader.downloadApp()
                    }
                }
            }
        }
        center {
            viewArea = textarea {
                isWrapText = true
            }
        }
    }

    override fun onDock() {
        super.onDock()
        changelogLoader = ChangelogLoader(
                listener = object : ChangelogLoader.Listener {
                    override fun onChangelog(changelog: Changelog) {
                        render(changelog)
                    }
                    override fun onNewVersion(changelog: Changelog) { }
                },
                versionProvider = VersionProviderImpl(api),
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        appDownloader = AppDownloader()
        appDownloader.onAttach()
        changelogLoader.onAttach()
        changelogLoader.load()
    }

    override fun onUndock() {
        appDownloader.onDetach()
        changelogLoader.onDetach()
        super.onUndock()
    }

    private fun render(changelog: Changelog) {
        viewLabelRemote.text = "Version available: ${changelog.version}"
        viewArea.text = changelog.contentAsString
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}