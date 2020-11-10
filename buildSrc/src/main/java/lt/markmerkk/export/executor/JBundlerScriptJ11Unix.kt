package lt.markmerkk.export.executor

import org.gradle.api.Project
import java.io.File
import lt.markmerkk.export.tasks.JBundleResource
import lt.markmerkk.export.utils.*
import java.lang.UnsupportedOperationException

/**
 * Script executor for Java8, Unix platform
 */
class JBundlerScriptJ11Unix(
        private val project: Project,
        private val bundleResource: JBundleResource
): JBundlerScriptProvider {

    private val jPackage = File(bundleResource.jdk14HomeDir, "/bin/jpackage")
    private val rootDir = project.rootDir
    private val scriptDir = File(bundleResource.scriptsDir, "/build-package-j11.sh")

    init {
        assert(jPackage.exists()) {
            "Cannot find 'jpackage' at path ${jPackage.absolutePath}"
        }
    }

    override fun scriptCommand(): List<String> {
        val scriptArgs = BuildScriptArgs(
                j11Home = bundleResource.jdk11HomeDir.absolutePath,
                j14Home = bundleResource.jdk14HomeDir.absolutePath,
                appVersion = bundleResource.versionName,
                appName = bundleResource.appName,
                appDescription = "",
                appVendor = "",
                appMainJar = bundleResource.mainJar.name,
                appMainClass = bundleResource.mainClassName,
                imageType = bundleResource.packageType,
                buildDir = project.buildDir.absolutePath,
                input = bundleResource.inputDir.absolutePath,
                output = bundleResource.bundlePath.absolutePath,
                appIcon = bundleResource.appIcon.absolutePath,
                jvmArgs = bundleResource.jvmOptions.joinToString(" ")
        )
        return listOf(
                "sh",
                scriptDir.absolutePath
        ).plus(scriptArgs.components)
    }

    override fun bundle(): String = throw UnsupportedOperationException()

    override fun debugPrint() {
        println("Using JDK11: ${bundleResource.jdk11HomeDir}")
        println("Using JRE: ${bundleResource.jdk14HomeDir}")
        println("Using Main JAR: ${bundleResource.mainJar}")
        println("Exec: ${scriptCommand()}")
    }

}