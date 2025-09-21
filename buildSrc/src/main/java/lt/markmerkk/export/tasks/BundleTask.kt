package lt.markmerkk.export.tasks

import lt.markmerkk.export.executor.JBundlerScriptProvider
import org.gradle.api.tasks.Exec
import java.io.File
import java.lang.IllegalStateException
import lt.markmerkk.export.executor.JBundlerScriptJ17Unix
import lt.markmerkk.export.executor.JBundlerScriptJ17Win
import org.gradle.kotlin.dsl.support.unzipTo

open class BundleTask: Exec() {

    private lateinit var bundleResource: JBundleResource
    private lateinit var scriptProvider: JBundlerScriptProvider

    fun init(
            appName: String,
            versionName: String,
            mainJarName: String,
            mainJarFilePath: String,
            mainClassName: String,
            mainIconFilePath: String,
            systemWide: Boolean,
            jvmProps: List<String>,
            scriptsDirPath: String,
            j17HomeOverride: String?,
            jmodsHomeOverride: String?,
    ) {
        val iconFile = File(mainIconFilePath)
        assert(iconFile.exists() && iconFile.isFile) {
            throw IllegalArgumentException("Cannot find app icon at $iconFile")
        }
        val tmpDistributions = File(project.buildDir, "/distributions/")
        val tmpDistributionsArchive = File(tmpDistributions, "${mainJarName}.zip")
        val tmpDir = File(project.buildDir, "/tmp2").apply { mkdirs() }
        val inputDir = File(project.buildDir, "/input").apply { mkdirs() }
        val resourceDir = File(project.projectDir, "/package/resources")
        val inputLibsDir = File(inputDir, "/libs/")
        val j17HomePath: String = j17HomeOverride ?: System.getenv("J17_HOME") ?: ""
        val jmodsHomePath: String = jmodsHomeOverride ?: System.getenv("JMODS_HOME") ?: ""
        bundleResource = JBundleResource(
            project = project,
            appName = appName,
            versionName = versionName,
            mainJarName = mainJarName,
            mainJarFilePath = mainJarFilePath,
            mainClassName = mainClassName,
            mainIconFilePath = mainIconFilePath,
            systemWide = systemWide,
            inputPath = inputDir.absolutePath,
            jdk17HomePath = j17HomePath,
            jmodsHomePath = jmodsHomePath,
            scriptsPath = scriptsDirPath,
            jvmOptions = jvmProps
        )
        scriptProvider = when (OsType.get()) {
            OsType.UNKNOWN -> throw IllegalStateException("Unsupported OS type")
            OsType.LINUX, OsType.MAC -> {
                JBundlerScriptJ17Unix(project, bundleResource)
            }
            OsType.WINDOWS -> {
                JBundlerScriptJ17Win(project, bundleResource)
            }
        }
        doFirst {
            debugPrint()
            println("Prepping environment")
            // Clean / creat input dir
            if (inputDir.exists()) {
                inputDir.deleteRecursively()
            }
            inputDir.mkdirs()

            // Clean / creat tmp dir
            if (tmpDir.exists()) {
                tmpDir.deleteRecursively()
            }
            tmpDir.mkdirs()

            // Clean / creat input/libs dir
            if (inputLibsDir.exists()) {
                inputLibsDir.deleteRecursively()
            }
            inputLibsDir.mkdirs()

            val inputResDir = File(inputDir, "/resources").apply { mkdirs() }
            println("Copying main jar: ${bundleResource.mainJar.name}")
            val inputMainJar = File(inputDir, "/${bundleResource.mainJar.name}")
                    .apply { createNewFile() }
            bundleResource.mainJar.copyTo(inputMainJar, overwrite = true)
            println("Copying resources")
            resourceDir.copyRecursively(inputResDir, overwrite = true)
            println("debugZip(archiveExist: ${tmpDistributionsArchive.exists()}, isFile: ${tmpDistributionsArchive.isFile})")
            println("Copying distributions: ${tmpDistributionsArchive.absolutePath}")
            unzipTo(tmpDir, tmpDistributionsArchive)
            val extractedLibDir = File(tmpDir, "/${bundleResource.mainJarName}/lib")
            println("Copying input libs (libsPath: ${extractedLibDir.absolutePath}, exists: ${extractedLibDir.exists()}, isDir: ${extractedLibDir.isDirectory})")
            val extractedLibFiles = extractedLibDir.listFiles()?.toList() ?: emptyList<File>()
            extractedLibFiles
                .filter { it.exists() }
            extractedLibFiles.forEach { sourceLibFile ->
                assert(inputLibsDir.isDirectory, { "inputLibsDirectory should be a directory" })
                // println(".copyLib(lib: ${sourceLibFile.absolutePath}, to: ${inputLibsDir.absolutePath})")
                val targetLibFile = File(inputLibsDir, "/${sourceLibFile.name}")
                    .apply { createNewFile() }
                sourceLibFile.copyTo(targetLibFile, overwrite = true)
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