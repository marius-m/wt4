package lt.markmerkk

import javafx.scene.Parent
import lt.markmerkk.interactors.KeepAliveGASession
import lt.markmerkk.interactors.KeepAliveGASessionImpl
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui_2.EmptyWidget
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.ConfigSetSettings
import lt.markmerkk.utils.Ticker
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.widgets.main.MainWidget
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.View
import tornadofx.stackpane
import javax.inject.Inject

class CoreWidget : View() {

    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var appConfig: Config
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var schedulersProvider: SchedulerProvider
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var autoSyncWatcher: AutoSyncWatcher2
    @Inject lateinit var ticker: Ticker
    @Inject lateinit var configSetSettings: ConfigSetSettings
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository

    private lateinit var keepAliveGASession: KeepAliveGASession

    private lateinit var viewActive: Fragment

    override val root: Parent = stackpane {
        viewActive = find<EmptyWidget>()
        add(viewActive)
    }

    fun attachMain() {
        // Init variables
        Main.component().inject(this)
        l.info("Launching ${appConfig.printPaths()}")
        val titleSuffix = if (BuildConfig.debug) "(DEBUG)" else ""
        title = "${BuildConfig.NAME} - ${BuildConfig.VERSION} $titleSuffix / Profile: ${configSetSettings.currentConfigOrDefault()}"

        // Init sevices
        userSettings.onAttach()
        syncInteractor.onAttach()

        tracker.sendEvent(
                GAStatics.CATEGORY_BUTTON,
                GAStatics.ACTION_START
        )
        keepAliveGASession = KeepAliveGASessionImpl(
            activeDisplayRepository,
            tracker,
            schedulersProvider.waitScheduler()
        )
        keepAliveGASession.onAttach()
        stageProperties.onAttach()
        autoSyncWatcher.onAttach()
        autoSyncWatcher.subscribeWatch()
        userSettings.changeOAuthPreset(
                host = BuildConfig.oauthHost,
                privateKey = BuildConfig.oauthKeyPrivate,
                consumerKey = BuildConfig.oauthKeyConsumer
        )
        ticker.onAttach()

        // Attaching view
        val newSidePanel = find<MainWidget>()
        viewActive.replaceWith(newSidePanel)
        viewActive = newSidePanel
    }

    fun detachMain() {
        ticker.onDetach()
        autoSyncWatcher.onDetach()
        stageProperties.onDetach()
        keepAliveGASession.onDetach()
        syncInteractor.onDetach()
        userSettings.onDetach()
        tracker.stop()

        val newSidePanel = find<EmptyWidget>()
        viewActive
                .replaceWith(newSidePanel)
        viewActive = newSidePanel
    }

    companion object {
        private val l = LoggerFactory.getLogger(CoreWidget::class.java)!!
    }

}