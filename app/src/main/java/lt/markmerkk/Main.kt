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
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.widgets.MainWidget
import org.apache.log4j.PatternLayout
import org.apache.log4j.Priority
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.RollingFileAppender
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class Main : App(), KeepAliveInteractor.Listener {

    @Inject lateinit var settings: UserSettings
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var keepAliveInteractor: KeepAliveInteractor
    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var autoUpdateInteractor: AutoUpdateInteractor
    @Inject lateinit var appConfig: Config
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var schedulersProvider: SchedulerProvider

    private lateinit var keepAliveGASession: KeepAliveGASession
    private lateinit var appComponent: AppComponent

    override val primaryView = MainWidget::class

    override fun start(stage: Stage) {
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this, StageProperties(stage)))
                .build()
        appComponent.inject(this)
        initLoggerSettings()

        DEBUG = appConfig.debug
        Translation.getInstance() // Initializing translations on first launch
        super.start(stage)

        logger.info("Running in " + config)
        stage.scene.stylesheets.add(javaClass.getResource("/css/material.css").toExternalForm())
        stage.scene.stylesheets.add(javaClass.getResource("/css/material_tree_table.css").toExternalForm())

        settings.onAttach()
        keepAliveInteractor.onAttach()
        keepAliveInteractor.register(this)
        syncInteractor.onAttach()

        stage.width = SCENE_WIDTH.toDouble()
        stage.height = SCENE_HEIGHT.toDouble()
        stage.minWidth = SCENE_WIDTH.toDouble()
        stage.minHeight = SCENE_HEIGHT.toDouble()
        stage.title
        tracker.sendEvent(
                GAStatics.CATEGORY_BUTTON,
                GAStatics.ACTION_START
        )
        keepAliveGASession = KeepAliveGASessionImpl(
                logStorage,
                tracker,
                schedulersProvider.waitScheduler()
        )
        keepAliveGASession.onAttach()
        stageProperties.onAttach()
    }

    override fun stop() {
        stageProperties.onDetach()
        keepAliveGASession.onDetach()
        syncInteractor.onDetach()
        keepAliveInteractor.unregister(this)
        keepAliveInteractor.onDetach()
        settings.onDetach()
        tracker.stop()
        InjectorNoDI.forgetAll()
        super.stop()
    }

    fun restart() {
        find<MainWidget>().showInfo("Restart app to take effect")
    }

    override fun update() {
        if (autoUpdateInteractor.isAutoUpdateTimeoutHit(System.currentTimeMillis())) {
            syncInteractor.syncAll()
        }
    }

    //region Convenience

    private fun initLoggerSettings() {
        PropertyConfigurator.configure(javaClass.getResource("/custom_log4j.properties"))
        val fileAppenderProd = RollingFileAppender(PatternLayout(LOG_LAYOUT_PROD), appConfig.cfgPath + "info_prod.log", true)
        fileAppenderProd.setMaxFileSize("100KB")
        fileAppenderProd.maxBackupIndex = 0
        fileAppenderProd.threshold = Priority.INFO
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderProd)

        val fileAppenderDebug = RollingFileAppender(PatternLayout(LOG_LAYOUT_DEBUG), appConfig.cfgPath + "info.log", true)
        fileAppenderDebug.setMaxFileSize("1MB")
        fileAppenderDebug.maxBackupIndex = 0
        fileAppenderDebug.threshold = Priority.INFO
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderDebug)

        val errorAppender = RollingFileAppender(PatternLayout(LOG_LAYOUT_DEBUG), appConfig.cfgPath + "debug.log", true)
        errorAppender.setMaxFileSize("1MB")
        errorAppender.maxBackupIndex = 0
        errorAppender.threshold = Priority.toPriority(Priority.ALL_INT)
        org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender)
    }

    //endregion

    companion object {
        const val LOG_LAYOUT_DEBUG = "%t / %d{dd-MMM-yyyy HH:mm:ss} %5p %c{1}:%L - %m%n"
        const val LOG_LAYOUT_PROD = "%d{dd-MMM-yyyy HH:mm:ss} %m%n"

        var DEBUG = false

        var SCENE_WIDTH = 600
        var SCENE_HEIGHT = 500

        @JvmStatic fun mainInstance(): Main = (FX.application as Main)
        @JvmStatic fun component(): AppComponent = (FX.application as Main).appComponent

        private val logger = LoggerFactory.getLogger(Main::class.java)!!
    }

}

fun main(args: Array<String>) {
    Application.launch(Main::class.java)
}
