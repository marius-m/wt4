package lt.markmerkk.export.executor

import org.gradle.api.Project
import java.io.File
import lt.markmerkk.export.tasks.JBundleResource
import lt.markmerkk.export.utils.*

/**
 * Script executor for Java8, Unix platform
 */
class JBundlerScriptJ8Unix(
        private val project: Project,
        private val bundleResource: JBundleResource
): JBundlerScriptProvider {

    private val packagerPath = File(bundleResource.jdkHomeDir, "/bin/javapackager")
    private val rootDir = project.rootDir

    init {
        assert(packagerPath.exists()) {
            "Cannot find 'javapackager' at path ${packagerPath.absolutePath}"
        }
    }

    override fun scriptCommand(): List<String> {
        val formatJvmOptions = bundleResource.jvmOptions
                .map { option -> "-BjvmOptions=$option" }
        return listOf(
                packagerPath.absolutePath,
                "-deploy",
                "-Bruntime=${bundleResource.jreHomeDir}",
                "-srcdir", bundleResource.appLibraryPath.absolutePath,
                "-srcfiles", bundleResource.mainJar.name,
                "-outdir", bundleResource.bundlePath.absolutePath,
                "-outfile", bundleResource.mainJar.name,
                "-appclass", bundleResource.mainClassName,
                "-native", bundleResource.packageType,
                "-name", bundleResource.appName,
                "-title", bundleResource.appName,
                "-v",
                "-nosign",
                "-Bicon=${bundleResource.appIcon.absolutePath}",
                "-BappVersion=${bundleResource.versionName}"
        ).plus(formatJvmOptions)
    }

    override fun bundle(): String {
        val workingDir: File = project.projectDir
        val scriptArgs = scriptCommand()
        val process = scriptCommand().execute(workingDir)
        return if (process.waitFor() == 0){
            process.text.trim()
        } else {
            val errorAsString = process.errorAsString()
            throw IllegalStateException("Error executing: $errorAsString")
        }
    }

    override fun debugPrint() {
        println("Using JDK: ${bundleResource.jdkHomeDir}")
        println("Using JRE: ${bundleResource.jreHomeDir}")
        println("Using Main JAR: ${bundleResource.mainJar}")
        println("Exec: ${scriptCommand()}")
    }

}