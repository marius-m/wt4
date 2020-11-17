package lt.markmerkk.export.executor

import org.gradle.api.Project
import java.io.File
import lt.markmerkk.export.tasks.JBundleResource
import java.lang.UnsupportedOperationException

/**
 * Script executor for Java11, Unix platform
 */
class JDepsScriptJ11Unix(
        private val project: Project,
        private val jdk11HomeDir: String,
        private val targetFileName: String
): JBundlerScriptProvider {

    private val jDeps = File(jdk11HomeDir, "/bin/jdeps")
    private val rootDir = project.rootDir
    private val dependencyDir = File(project.buildDir, "/install/app/lib")
    private val targetFile = File(dependencyDir, "/${targetFileName}")

    init {
        assert(jDeps.exists()) {
            "Cannot find 'jdeps' at path ${jDeps.absolutePath}"
        }
    }

    override fun scriptCommand(): List<String> {
        if (!dependencyDir.exists() || !dependencyDir.isDirectory) {
            throw java.lang.IllegalStateException("No library path @${dependencyDir.absolutePath}")
        }
        val dependencyFiles: Array<File> = dependencyDir.listFiles() ?: emptyArray()
        val jarFiles: List<File> = dependencyFiles
                .toList()
                .filter { it.isFile && it.extension.endsWith("jar") }
        val javaLibsAsString = dependencyFiles
                .map { "${dependencyDir.absolutePath}/${it.name}" }
                .joinToString(":")
        val depsAsString = listOf(
                jDeps.absolutePath,
                "--multi-release",
                "11",
                "--class-path",
                "\"${javaLibsAsString}\"",
                "-R",
                "-summary",
                targetFile.absolutePath
        )
        return depsAsString
    }

    override fun bundle(): String = throw UnsupportedOperationException()

    override fun debugPrint() {
        println("Exec: ${scriptCommand()}")
    }

}