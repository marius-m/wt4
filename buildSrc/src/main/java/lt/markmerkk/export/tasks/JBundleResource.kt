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
        val mainClassName: String,
        val jvmOptions: List<String>
) {
    val buildDir = project.buildDir
    val applicationLibraryPath = File("${buildDir}/libs/")
    val mainJar = File("${applicationLibraryPath.absolutePath}/${appName}-${versionName}.jar")
    val bundlePath = File("${buildDir}/bundle")

    val appIcon: File = when (OsType.get()) {
        OsType.UNKNOWN -> throw IllegalStateException("Cannot build on unrecognized system")
        OsType.MAC -> File("icons/mac/AppIcon.icns")
        OsType.LINUX -> File("icons/mac/App1024.png")
        OsType.WINDOWS -> File("icons/windows/AppIcon.ico")
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
               "-Dlog4j.configurationFile=prod_log4j2.xml"
       )
    }

}