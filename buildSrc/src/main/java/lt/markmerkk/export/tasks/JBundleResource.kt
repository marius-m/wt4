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
        private val jdkHomePath: String,
        private val jreHomePath: String,
        val jvmOptions: List<String>
) {

    val mainIconFile = File(mainIconFilePath)
    val appIconName = File(mainIconFilePath).nameWithoutExtension
    val appIcon: File = when (OsType.get()) {
        OsType.UNKNOWN -> throw IllegalStateException("Cannot build on unrecognized system")
        OsType.MAC -> File(mainIconFile.parent, "/mac/${appIconName}.icns")
        OsType.LINUX -> File(mainIconFile.parent, "/${appIconName}.png")
        OsType.WINDOWS -> File(mainIconFile.parent, "/windows/${appIconName}.ico")
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
    val appLibraryPath = File(mainJar.parent)
    val bundlePath = File("${buildDir}/bundle")
    val jdkHomeDir: File = File(jdkHomePath)
    val jreHomeDir: File = if (jreHomePath.isNotEmpty()) {
        File(jreHomePath)
    } else {
        File(jdkHomeDir, "/jre")
    }

    val packageType: String = when (OsType.get()) {
        OsType.UNKNOWN -> ""
        OsType.MAC -> "dmg"
        OsType.LINUX -> "deb"
        OsType.WINDOWS -> "exe"
    }

    companion object {
       val jvmOptionsDefault: List<String> = listOf(
               "-Xms128m",
               "-Xmx300m",
               "-XX:+UseG1GC",
               "-Dlog4j.configurationFile=prod_log4j2.xml" // todo move this to
       )
    }

}