package lt.markmerkk.export.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input

open class GenIconMac: Exec() {
    init {
        commandLine(
                "sh",
                project.file("${project.projectDir}/scripts/mac-icon.sh"),
                project.file("${project.projectDir}/icons/App1024.png"),
                project.file("${project.projectDir}/icons/mac"),
                "AppIcon")
    }
}

open class GenIconWindows: Exec() {
    init {
        commandLine(
                "sh",
                project.file("${project.projectDir}/scripts/windows-icon.sh"),
                project.file("${project.projectDir}/icons/App1024.png"),
                project.file("${project.projectDir}/icons/windows"),
                "AppIcon")
    }
}

open class GenIcons: DefaultTask() {
    init {
        dependsOn(
                GenIconMac::class,
                GenIconWindows::class)
    }
}
