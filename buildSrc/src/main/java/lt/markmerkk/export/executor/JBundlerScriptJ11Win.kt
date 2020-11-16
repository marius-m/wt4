package lt.markmerkk.export.executor

import lt.markmerkk.export.tasks.JBundleResource
import org.gradle.api.Project
import java.io.File

/**
 * Script executor for Java11, Win platform
 */
class JBundlerScriptJ11Win(
        private val project: Project,
        private val bundleResource: JBundleResource
): JBundlerScriptProvider {

    private val jPackage = File(bundleResource.jdk14HomeDir, "${File.separator}bin${File.separator}jpackage")
    private val rootDir = project.rootDir
    private val scriptDir = File(bundleResource.scriptsDir, "${File.separator}build-package-j11.bat")

    init {
        assert(jPackage.exists()) {
            "Cannot find 'jpackage' at path ${jPackage.absolutePath}"
        }
    }

    override fun scriptCommand(): List<String> {
        val winArgs = mutableListOf(
                "--win-shortcut",
                "--win-menu"
        )
        if (!bundleResource.systemWide) {
            winArgs.plus("--win-per-user-install")
            winArgs.plus("--win-dir-chooser")
        }
        val scriptArgs = BuildScriptArgs(
                j11Home = bundleResource.jdk11HomeDir.absolutePath,
                j14Home = bundleResource.jdk14HomeDir.absolutePath,
                appVersion = bundleResource.versionName,
                appName = bundleResource.appName,
                appDescription = "Jira worklog app",
                appVendor = "MM",
                appMainJar = bundleResource.mainJar.name,
                appMainClass = bundleResource.mainClassName,
                imageType = bundleResource.packageType,
                buildDir = project.buildDir.absolutePath,
                input = bundleResource.inputDir.absolutePath,
                output = bundleResource.bundlePath.absolutePath,
                appIcon = bundleResource.appIcon.absolutePath,
                jvmArgs = bundleResource.jvmOptions.joinToString(" "),
                platformArgs = winArgs
        )
        return listOf("cmd", "/C", scriptDir.absolutePath).plus(scriptArgs.components)
    }

    override fun bundle(): String = throw UnsupportedOperationException()

    override fun debugPrint() {
        println("Using JDK11: ${bundleResource.jdk11HomeDir}")
        println("Using JRE: ${bundleResource.jdk14HomeDir}")
        println("Using Main JAR: ${bundleResource.mainJar}")
        println("Exec: ${scriptCommand()}")
    }

}