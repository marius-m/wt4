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
        private val jdkHomeDir: File,
        private val jreHomeDir: File,
        private val bundlerResource: JBundleResource
): JBundlerScriptProvider {

    private val packagerPath = File(jdkHomeDir, "/bin/javapackager")
    private val rootDir = project.rootDir

    init {
        assert(packagerPath.exists()) {
            "Cannot find 'javapackager' at path ${packagerPath.absolutePath}"
        }
    }

    override fun scriptCommand(): List<String> {
        val formatJvmOptions = bundlerResource.jvmOptions
                .map { option -> "-BjvmOptions=$option" }
        return listOf(
                packagerPath.absolutePath,
                "-deploy",
                "-Bruntime=${jreHomeDir}",
                "-srcdir", bundlerResource.applicationLibraryPath.absolutePath,
                "-srcfiles", bundlerResource.mainJar.absolutePath,
                "-outdir", bundlerResource.bundlePath.absolutePath,
                "-outfile", bundlerResource.mainJar.name,
                "-appclass", bundlerResource.mainClassName,
                "-native", bundlerResource.packageType,
                "-name", bundlerResource.appName,
                "-title", bundlerResource.appName,
                "-v",
                "-nosign",
                "-Bicon", bundlerResource.appIcon.absolutePath,
                "-BappVersion", bundlerResource.versionName
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

}