import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    id("base")
    id("kotlin")
    id("lt.markmerkk.export")
}

val versionName: String by project
val publishVersion: String = versionName
val applicationLibraryPath: File = file("${rootProject.project(":app").buildDir}/libs/")
val mainJar: File = file("${applicationLibraryPath.absolutePath}/app-${publishVersion}.jar")
val bundlePath: File = file("${buildDir}/bundle")
val bundlerScript: File = when (OsType.get()) {
    OsType.UNKNOWN -> throw IllegalStateException("Cannot build on unrecognized system")
    OsType.MAC, OsType.LINUX -> file("scripts/build-package.sh")
    OsType.WINDOWS -> file("scripts/build-package.bat")
}
val appIcon: File = when (OsType.get()) {
    OsType.UNKNOWN -> throw IllegalStateException("Cannot build on unrecognized system")
    OsType.MAC -> file("icons/mac/AppIcon.icns")
    OsType.LINUX -> file("icons/mac/App1024.png")
    OsType.WINDOWS -> file("icons/windows/AppIcon.ico")
}
val packageType: String = when (OsType.get()) {
    OsType.UNKNOWN -> ""
    OsType.MAC -> "dmg"
    OsType.LINUX -> "deb"
    OsType.WINDOWS -> "exe"
}

ext {
    set("publishVersion", publishVersion)
    set("applicationLibraryPath", applicationLibraryPath)
    set("mainJar", mainJar)
    set("bundlePath", bundlePath)
    set("appIcon", appIcon)
    set("packageType", packageType)
}

//tasks.register("recreateIconMacApp", Exec::class.java) {
//    group = "Package"
//    description = "Regenerate the Mac application icon"
//    commandLine(
//            "sh",
//            file("scripts/mac-icon.sh"),
//            file("icons/App1024.png"),
//            file("icons/mac"),
//            "AppIcon")
//}
//
//tasks.register("recreateIconWindowsApp", Exec::class.java) {
//    group = "Package"
//    description = "Regenerate the Windows application icon"
//    commandLine(
//            "sh",
//            file("scripts/windows-icon.sh"),
//            file("icons/App1024.png"),
//            file("icons/windows"),
//            "AppIcon")
//}
//
//tasks.register("recreateIcons") {
//    group = "Package"
//    description = "Regenerate all the application bundle icons"
//    dependsOn("recreateIconMacApp", "recreateIconWindowsApp")
//}

tasks.register("checkBundlingEnvironment") {
    group = "Package"
    description = "Check the environment for building the installable bundle"
    doLast {
        if (!mainJar.exists()) {
            throw GradleException("Main file does not exist: $mainJar")
        }
    }
}

tasks.register("createBundle", Exec::class.java) {
    group = "Package"
    description = "Build the installable bundle"
    dependsOn("checkBundlingEnvironment")
    val commandLineArray: Array<String> = when (OsType.get()) {
        OsType.UNKNOWN -> throw IllegalStateException("Cannot resolve OS type")
        OsType.MAC, OsType.LINUX -> {
            arrayOf(
                "sh",
                bundlerScript.absolutePath,
                packageType,
                applicationLibraryPath.absolutePath,
                bundlePath.absolutePath,
                mainJar.name,
                publishVersion,
                appIcon.absolutePath
            )
        }
        OsType.WINDOWS -> {
            arrayOf(
                    "cmd",
                    "/c",
                    bundlerScript.absolutePath,
                    packageType,
                    applicationLibraryPath.absolutePath,
                    bundlePath.absolutePath,
                    mainJar.name,
                    publishVersion,
                    appIcon.absolutePath
            )
        }
    }
    commandLine(commandLineArray)
}

enum class OsType {
    UNKNOWN,
    MAC,
    LINUX,
    WINDOWS,
    ;

    companion object {
        fun get(): OsType {
            return when {
                Os.isFamily(Os.FAMILY_MAC) -> {
                    MAC
                }
                Os.isFamily(Os.FAMILY_UNIX) -> {
                    LINUX
                }
                Os.isFamily(Os.FAMILY_WINDOWS) -> {
                    WINDOWS
                }
                else -> {
                    UNKNOWN
                }
            }
        }
    }

}

