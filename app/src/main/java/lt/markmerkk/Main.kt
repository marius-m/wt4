package lt.markmerkk

import com.vinumeris.updatefx.AppDirectory
import com.vinumeris.updatefx.Crypto
import com.vinumeris.updatefx.UpdateFX
import com.vinumeris.updatefx.Updater
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.dagger.components.AppComponent
import lt.markmerkk.dagger.components.DaggerAppComponent
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.interactors.*
import lt.markmerkk.ui_2.MainView2
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.tracker.ITracker
import org.apache.log4j.PatternLayout
import org.apache.log4j.Priority
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.RollingFileAppender
import org.slf4j.LoggerFactory
import rx.schedulers.Schedulers
import java.net.URI
import javax.inject.Inject

class Main : Application(), KeepAliveInteractor.Listener {
    @Inject
    lateinit var settings: UserSettings
    @Inject
    lateinit var logStorage: LogStorage
    @Inject
    lateinit var keepAliveInteractor: KeepAliveInteractor
    @Inject
    lateinit var syncInteractor: SyncInteractor
    @Inject
    lateinit var autoUpdateInteractor: AutoUpdateInteractor
    @Inject
    lateinit var config: Config
    @Inject
    lateinit var tracker: ITracker

    lateinit var primaryStage: Stage

    var keepAliveGASession: KeepAliveGASession? = null

    override fun start(stage: Stage) {
        this.primaryStage = stage
        mainInstance = this
        initApp(primaryStage)
    }

    override fun stop() {
        destroyApp()
        mainInstance = null
        super.stop()
    }

    fun restart() {
        destroyApp()
        initApp(primaryStage)
    }

    //region DI

    fun initApp(stage: Stage) {
        Thread.currentThread().contextClassLoader = Main::class.java.classLoader
        AppDirectory.initAppDir("WT4")

        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this, StageProperties(stage)))
                .build()
        component!!.inject(this)
        initLoggerSettings()

        val pubkeys = Crypto.decode("03240B61A44F940A85443C7C6CBDD7141B65619AAB66B3D867630B82AA10453BD6")
        logger.debug("Initialize updater")
        val updater = object : Updater(URI.create("http://89.40.3.216/wt4_2/index"), "WT4/${settings.version}",
                AppDirectory.dir(), UpdateFX.findCodePath(Main::class.java), pubkeys, 1) {
            override fun updateProgress(workDone: Long, max: Long) {
                super.updateProgress(workDone, max)
                // Give UI a chance to show.
//                Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS)
                logger.debug("Update progress: workDone: ${workDone} / max: ${max}")
            }
        }

        logger.info("Checking for updates!")
        updater.setOnSucceeded { event ->
            try {
                val summary = updater.get()
                if (summary.descriptions.size > 0) {
                    logger.info("One liner: {}", summary.descriptions[0].oneLiner)
                    logger.info("{}", summary.descriptions[0].description)
                }
                if (summary.highestVersion > config.versionCode) {
                    logger.info("Restarting to get version " + summary.highestVersion)
                    if (UpdateFX.getVersionPin(AppDirectory.dir()) == 0)
                        UpdateFX.restartApp()
                }
            } catch (e: Throwable) {
                logger.error("oops", e)
            }
        }
        updater.setOnFailed { event ->
            logger.error("Update error: {}", updater.exception)
            updater.exception.printStackTrace()
        }
        Thread(updater, "UpdateFX Thread").start()

        DEBUG = config.debug
        Translation.getInstance() // Initializing translations on first launch
        logger.info("Running in " + config)

        settings.onAttach()
        keepAliveInteractor.onAttach()
        keepAliveInteractor.register(this)
        syncInteractor.onAttach()

        stage.width = SCENE_WIDTH.toDouble()
        stage.height = SCENE_HEIGHT.toDouble()
        stage.minWidth = SCENE_WIDTH.toDouble()
        stage.minHeight = SCENE_HEIGHT.toDouble()
        stage.scene = initScene(stage)
        stage.show()
        val titlePrefix = if (config.debug) " !!DEBUG!! " else ""
        stage.title = "WT4 (${config.versionName}-${config.versionCode}) ${titlePrefix}"
        tracker.sendEvent(
                GAStatics.CATEGORY_BUTTON,
                GAStatics.ACTION_START
        )
        keepAliveGASession = KeepAliveGASessionImpl(
                logStorage,
                tracker,
                Schedulers.computation())
        keepAliveGASession?.onAttach()
    }

    fun initScene(stage: Stage): Scene {
        val stackContainer = StackPane(MainView2().view)
        val scene = Scene(stackContainer)
        stackContainer.prefWidthProperty().bind(scene.widthProperty())
        stackContainer.prefHeightProperty().bind(scene.heightProperty())
        scene.stylesheets.add(javaClass.getResource("/css/material.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/css/material_tree_table.css").toExternalForm())
        return scene
    }

    fun destroyApp() {
        keepAliveGASession?.onDetach()
        syncInteractor.onDetach()
        keepAliveInteractor.unregister(this)
        keepAliveInteractor.onDetach()
        settings.onDetach()
        tracker.stop()
        InjectorNoDI.forgetAll()
    }

    //endregion

    override fun update() {
        if (autoUpdateInteractor.isAutoUpdateTimeoutHit(System.currentTimeMillis())) {
            syncInteractor.syncAll()
        }
    }

    //region Convenience

    private fun initLoggerSettings() {
        PropertyConfigurator.configure(javaClass.getResource("/custom_log4j.properties"))
        val fileAppenderProd = RollingFileAppender(PatternLayout(LOG_LAYOUT_PROD), config.cfgPath + "info_prod.log", true)
        fileAppenderProd.setMaxFileSize("100KB")
        fileAppenderProd.maxBackupIndex = 0
        fileAppenderProd.threshold = Priority.INFO
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderProd)

        val fileAppenderDebug = RollingFileAppender(PatternLayout(LOG_LAYOUT_DEBUG), config.cfgPath + "info.log", true)
        fileAppenderDebug.setMaxFileSize("1MB")
        fileAppenderDebug.maxBackupIndex = 0
        fileAppenderDebug.threshold = Priority.INFO
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderDebug)

        val errorAppender = RollingFileAppender(PatternLayout(LOG_LAYOUT_DEBUG), config.cfgPath + "debug.log", true)
        errorAppender.setMaxFileSize("1MB")
        errorAppender.maxBackupIndex = 0
        errorAppender.threshold = Priority.toPriority(Priority.ALL_INT)
        org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender)
    }

    //endregion

    companion object {
        val LOG_LAYOUT_DEBUG = "%t / %d{dd-MMM-yyyy HH:mm:ss} %5p %c{1}:%L - %m%n"
        val LOG_LAYOUT_PROD = "%d{dd-MMM-yyyy HH:mm:ss} %m%n"

        var DEBUG = false

        var SCENE_WIDTH = 600
        var SCENE_HEIGHT = 500

        var component: AppComponent? = null
        var mainInstance: Main? = null

        private val logger = LoggerFactory.getLogger(Main::class.java)!!
    }

}
