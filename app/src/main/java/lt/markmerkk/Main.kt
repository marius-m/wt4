package lt.markmerkk

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.dagger.components.AppComponent
import lt.markmerkk.dagger.components.DaggerAppComponent
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.interactors.*
import lt.markmerkk.ui.MainView
import lt.markmerkk.ui_2.MainView2
import lt.markmerkk.utils.tracker.ITracker
import org.apache.log4j.PatternLayout
import org.apache.log4j.Priority
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.RollingFileAppender
import org.slf4j.LoggerFactory
import rx.schedulers.Schedulers
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
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        component!!.inject(this)
        initLoggerSettings()

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
        stage.title = "WT4"
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
        if (MATERIAL) {
            val stackContainer = StackPane(MainView2().view)
            val scene = Scene(stackContainer)
            stackContainer.prefWidthProperty().bind(scene.widthProperty())
            stackContainer.prefHeightProperty().bind(scene.heightProperty())
            scene.stylesheets.add(javaClass.getResource("/css/material.css").toExternalForm())
            return scene
        }
        val mainView = MainView(stage)
        val scene = Scene(mainView.view)
        val cssResource1 = javaClass.getResource("/text-field-red-border.css").toExternalForm()
        scene.stylesheets.add(cssResource1)
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
        var MATERIAL = false

        var SCENE_WIDTH = 600
        var SCENE_HEIGHT = 500

        var component: AppComponent? = null
        var mainInstance: Main? = null

        private val logger = LoggerFactory.getLogger(Main::class.java)!!
    }

}
