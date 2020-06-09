package lt.markmerkk.export.icons

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Exec

open class GenIconMac: Exec() {

    private lateinit var scriptProvider: IconScriptProvider

    fun init(
            mainIconFilePath: String,
            scriptsDirPath: String
    ) {
        scriptProvider = IconScriptProviderGeneric(
                project = project,
                mainIconFilePath = mainIconFilePath,
                scriptsDirPath = scriptsDirPath,
                scriptFileName = "mac-icon.sh",
                destinationDirName = "mac"
        )
        workingDir = project.projectDir
        setCommandLine(*scriptProvider.scriptCommand().toTypedArray())
    }

    fun debugPrint() {
        scriptProvider.debugPrint()
    }

}

open class GenIconWindows: Exec() {

    private lateinit var scriptProvider: IconScriptProvider

    fun init(
            mainIconFilePath: String,
            scriptsDirPath: String
    ) {
        scriptProvider = IconScriptProviderGeneric(
                project = project,
                mainIconFilePath = mainIconFilePath,
                scriptsDirPath = scriptsDirPath,
                scriptFileName = "windows-icon.sh",
                destinationDirName = "win"
        )
        workingDir = project.projectDir
        setCommandLine(*scriptProvider.scriptCommand().toTypedArray())
    }

    fun debugPrint() {
        scriptProvider.debugPrint()
    }

}

open class GenIcons: DefaultTask() {
    init {
        dependsOn(
                GenIconMac::class,
                GenIconWindows::class)
    }
}
