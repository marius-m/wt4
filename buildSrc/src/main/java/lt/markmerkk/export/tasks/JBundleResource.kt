package lt.markmerkk.export.tasks

import org.gradle.api.Project
import java.io.File

/**
 * Provide all necessary data for bundling app
 */
data class JBundleResource(
        val project: Project,
        val appName: String,
        val versionName: String,
        val mainJarFilePath: String,
        val mainClassName: String,
        val mainIconFilePath: String,
        val systemWide: Boolean,
        private val inputPath: String,
        private val jdk8HomePath: String,
        private val jre8HomePath: String,
        private val jdk11HomePath: String,
        private val jdk14HomePath: String,
        private val scriptsPath: String,
        val jvmOptions: List<String>
) {

    val mainIconFile = File(mainIconFilePath)
    val appIconName = File(mainIconFilePath).nameWithoutExtension
    val appIcon: File = when (OsType.get()) {
        OsType.UNKNOWN -> throw IllegalStateException("Cannot build on unrecognized system")
        OsType.MAC -> File(mainIconFile.parent, "/mac/${appIconName}.icns")
        OsType.LINUX -> File(mainIconFile.parent, "/${appIconName}.png")
        OsType.WINDOWS -> File(mainIconFile.parent, "/win/${appIconName}.ico")
    }
    val mainJar = File(mainJarFilePath)
    val jarName = mainJar.nameWithoutExtension

    init {
        assert(mainClassName.isNotEmpty()) {
            throw IllegalStateException("No main class defined!")
        }
        assert(mainIconFile.exists() && mainIconFile.isFile) {
            throw IllegalStateException("No app icon at $mainIconFile")
        }
        assert(appIcon.exists() && appIcon.isFile) {
            throw IllegalStateException("No app icon at $appIconName")
        }
        assert(mainJar.exists() && mainJar.isFile) {
            throw IllegalStateException("No main JAR at $mainJar")
        }
    }

    val buildDir = project.buildDir
    val bundlePath = File("${buildDir}/bundle")
    val jdk8HomeDir: File = File(jdk8HomePath)
    val jre8HomeDir: File = if (jre8HomePath.isNotEmpty()) {
        File(jre8HomePath)
    } else {
        File(jdk8HomeDir, "/jre")
    }
    val jdk11HomeDir: File = File(jdk11HomePath)
    val jdk14HomeDir: File = File(jdk14HomePath)
    val scriptsDir: File = File(scriptsPath)
    val inputDir = File(inputPath)

    val packageType: String = when (OsType.get()) {
        OsType.UNKNOWN -> ""
        OsType.MAC -> "dmg"
        OsType.LINUX -> "deb"
        OsType.WINDOWS -> "msi"
    }

    companion object {
       val jvmOptionsDefault: List<String> = listOf(
               "-Xms128m",
               "-Xmx300m",
               "-XX:+UseG1GC"
       )
    }

}