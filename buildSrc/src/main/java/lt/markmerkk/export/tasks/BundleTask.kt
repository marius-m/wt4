package lt.markmerkk.export.tasks

import lt.markmerkk.export.executor.JBundlerScriptJ8Unix
import lt.markmerkk.export.executor.JBundlerScriptProvider
import org.gradle.api.tasks.Exec
import java.io.File
import java.lang.IllegalStateException

open class BundleTask: Exec() {

    private lateinit var bundleResource: JBundleResource
    private lateinit var scriptProvider: JBundlerScriptProvider

    fun init(
            appName: String,
            versionName: String,
            mainJarFilePath: String,
            mainClassName: String,
            mainIconFilePath: String
    ) {
        val iconFile = File(mainIconFilePath)
        assert(iconFile.exists() && iconFile.isFile) {
            throw IllegalArgumentException("Cannot find app icon at $iconFile")
        }
        bundleResource = JBundleResource(
                project = project,
                appName = appName,
                versionName = versionName,
                mainJarFilePath = mainJarFilePath,
                mainClassName = mainClassName,
                mainIconFilePath = mainIconFilePath,
                jdkHomePath = File(System.getenv("JAVA_HOME")).absolutePath,
                jreHomePath = File(System.getenv("JRE_HOME")).absolutePath,
                jvmOptions = JBundleResource.jvmOptionsDefault
        )
        scriptProvider = when (OsType.get()) {
            OsType.UNKNOWN -> throw IllegalStateException("Unsupported OS type")
            OsType.LINUX, OsType.MAC -> {
                JBundlerScriptJ8Unix(
                    project = project,
                    bundleResource = bundleResource
                )
            }
            OsType.WINDOWS -> {
                JBundlerScriptJ8Unix(
                        project = project,
                        bundleResource = bundleResource
                )
            }
        }
        val scriptArgs = scriptProvider.scriptCommand()
        workingDir = project.projectDir
        setCommandLine(*scriptArgs.toTypedArray())
    }

    fun debugPrint() {
        scriptProvider.debugPrint()
    }

}