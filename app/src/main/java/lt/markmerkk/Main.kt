package lt.markmerkk

import io.sentry.Sentry
import io.sentry.SentryOptions
import javafx.stage.Stage
import lt.markmerkk.dagger.components.AppComponent
import lt.markmerkk.dagger.components.DaggerAppComponent
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.ui_2.StageProperties
import org.slf4j.LoggerFactory
import tornadofx.App
import tornadofx.FX
import tornadofx.find
import java.awt.SplashScreen
import java.lang.management.ManagementFactory

class Main : App(CoreWidget::class, Styles::class) {

    private lateinit var appComponent: AppComponent

    override fun start(stage: Stage) {
        generateGraph(stage)
        super.start(stage)
        initSentry()
        printArguments()
        stage.width = SCENE_WIDTH.toDouble()
        stage.height = SCENE_HEIGHT.toDouble()
        stage.minWidth = SCENE_WIDTH.toDouble()
        stage.minHeight = SCENE_HEIGHT.toDouble()
        stage.title
        find<CoreWidget>().attachMain()
        SplashScreen.getSplashScreen()?.close()
    }

    override fun stop() {
        find<CoreWidget>().detachMain()
        super.stop()
    }

    private fun generateGraph(stage: Stage) {
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this, StageProperties(stage)))
                .build()
        appComponent.inject(this)
    }

    private fun initSentry() {
        Sentry.init(SentryOptions().apply {
            dsn = BuildConfig.sentryDsn
            environment = "${BuildConfig.NAME}-${BuildConfig.flavor} (debug=${BuildConfig.debug})"
            release = "${BuildConfig.versionName}-${BuildConfig.versionCode}"
        })
    }

    private fun printArguments() {
        ManagementFactory
            .getRuntimeMXBean()
            .getInputArguments()
            .forEachIndexed { index, arg ->
                logger.info("ARG${index}: '${arg}'")
            }
    }

    companion object {

        var SCENE_WIDTH = 800
        var SCENE_HEIGHT = 600

        @JvmStatic
        fun component(): AppComponent {
            return (FX.application as Main).appComponent
        }

        @JvmStatic
        fun restart() {
            val mainInstance = (FX.application as Main)
            val coreWidget = find<CoreWidget>()
            coreWidget.detachMain()
            mainInstance.generateGraph(coreWidget.primaryStage)
            coreWidget.attachMain()
        }

        private val logger = LoggerFactory.getLogger(Main::class.java)!!
    }
}

