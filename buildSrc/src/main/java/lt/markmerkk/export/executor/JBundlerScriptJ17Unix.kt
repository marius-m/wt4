package lt.markmerkk.export.executor

import org.gradle.api.Project
import java.io.File
import lt.markmerkk.export.tasks.JBundleResource
import java.lang.UnsupportedOperationException

/**
 * Script executor for Java11, Unix platform
 */
class JBundlerScriptJ17Unix(
        private val project: Project,
        private val bundleResource: JBundleResource
): JBundlerScriptProvider {

    private val jPackage = File(bundleResource.jdk17HomeDir, "/bin/jpackage")
    private val rootDir = project.rootDir
    private val scriptFile = File(bundleResource.scriptsDir, "/build-package-j17.sh")

    init {
        assert(jPackage.exists()) {
            "Cannot find 'jpackage' at path ${jPackage.absolutePath}"
        }
    }

    override fun scriptCommand(): List<String> {
        val scriptArgs = BuildScriptArgs(
            javaVersion = "17",
            j17Home = bundleResource.jdk17HomeDir.absolutePath,
            jmodsHome = bundleResource.jmodsHomeDir.absolutePath,
            appVersion = bundleResource.versionName,
            appName = bundleResource.appName,
            appDescription = "Jira worklog app",
            appVendor = "MM",
            appMainJar = bundleResource.mainJar.name,
            appMainClass = bundleResource.mainClassName,
            imageType = bundleResource.packageType,
            buildDir = project.buildDir.absolutePath,
            inputLibsDir = bundleResource.inputLibsDir.absolutePath,
            input = bundleResource.inputDir.absolutePath,
            output = bundleResource.bundlePath.absolutePath,
            appIcon = bundleResource.appIcon.absolutePath,
            jvmArgs = bundleResource.jvmOptions.joinToString(" "),
            platformArgs = emptyList()
        )
        return listOf(
                "sh",
                scriptFile.absolutePath
        ).plus(scriptArgs.components)
    }

    override fun bundle(): String = throw UnsupportedOperationException()

    override fun debugPrint() {
        println("Using JDK17: ${bundleResource.jdk17HomeDir}")
        println("Using JMODS: ${bundleResource.jmodsHomeDir}")
        println("Using Main JAR: ${bundleResource.mainJar}")
        println("Exec: ${scriptCommand()}")
    }

}