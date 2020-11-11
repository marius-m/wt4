package lt.markmerkk.export.tasks

import lt.markmerkk.export.executor.JBundlerScriptJ11Unix
import lt.markmerkk.export.executor.JBundlerScriptJ11Win
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
            mainIconFilePath: String,
            systemWide: Boolean,
            jvmProps: List<String>,
            scriptsDirPath: String
    ) {
        val iconFile = File(mainIconFilePath)
        assert(iconFile.exists() && iconFile.isFile) {
            throw IllegalArgumentException("Cannot find app icon at $iconFile")
        }
        val inputDir = File(project.buildDir, "/input")
        val resourceDir = File(project.projectDir, "/package/resources")
        val jdk8HomePath: String = System.getenv("JAVA_HOME") ?: ""
        val jre8HomePath: String = System.getenv("JRE_HOME") ?: ""
        val j11HomePath: String = System.getenv("J11_HOME") ?: ""
        val j14HomePath: String = System.getenv("J14_HOME") ?: ""
        bundleResource = JBundleResource(
                project = project,
                appName = appName,
                versionName = versionName,
                mainJarFilePath = mainJarFilePath,
                mainClassName = mainClassName,
                mainIconFilePath = mainIconFilePath,
                systemWide = systemWide,
                inputPath = inputDir.absolutePath,
                jdk8HomePath = jdk8HomePath,
                jre8HomePath = jre8HomePath,
                jdk11HomePath = j11HomePath,
                jdk14HomePath = j14HomePath,
                scriptsPath = scriptsDirPath,
                jvmOptions = jvmProps
        )
        scriptProvider = when (OsType.get()) {
            OsType.UNKNOWN -> throw IllegalStateException("Unsupported OS type")
            OsType.LINUX, OsType.MAC -> {
                JBundlerScriptJ11Unix(project, bundleResource)
            }
            OsType.WINDOWS -> {
                JBundlerScriptJ11Win(project, bundleResource)
            }
        }
        doFirst {
            debugPrint()
            if (inputDir.exists()) {
                inputDir.deleteRecursively()
            }
            inputDir.mkdirs()
            val inputResDir = File(inputDir, "/resources").apply { mkdirs() }
            val inputMainJar = File(inputDir, "/${bundleResource.mainJar.name}")
                    .apply { createNewFile() }
            bundleResource.mainJar.copyTo(inputMainJar, overwrite = true)
            resourceDir.copyRecursively(inputResDir, overwrite = true)
        }
        val scriptArgs = scriptProvider.scriptCommand()
        workingDir = project.projectDir
        setCommandLine(*scriptArgs.toTypedArray())
    }

    fun debugPrint() {
        scriptProvider.debugPrint()
    }

}