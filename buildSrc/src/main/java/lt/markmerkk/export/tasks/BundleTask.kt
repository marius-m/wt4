package lt.markmerkk.export.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import lt.markmerkk.export.executor.JBundlerScriptProvider
import lt.markmerkk.export.executor.JBundlerScriptJ8Unix
import javax.inject.Inject

open class BundleTask @Inject constructor(
        private val appName: String,
        private val versionName: String,
        private val mainClassName: String
): DefaultTask() {

    @TaskAction
    fun run() {
        println("Gather resources")
        val bundleResource = JBundleResource(
                project = project,
                appName = appName,
                versionName = versionName,
                mainClassName = mainClassName,
                jvmOptions = JBundleResource.jvmOptionsDefault
        )
        println("Generate export script")
        val scriptExec: JBundlerScriptProvider = JBundlerScriptJ8Unix(
                project = project,
                jdkHomeDir = File(System.getenv("JAVA_HOME")),
                jreHomeDir = File(System.getenv("JAVA_HOME")),
                bundlerResource = bundleResource
        )
        val scriptArgs = scriptExec.scriptCommand()
        println("Executing: $scriptArgs")
        scriptExec
                .bundle()
    }

}