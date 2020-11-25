package lt.markmerkk.export.executor

import java.lang.IllegalArgumentException

data class BuildScriptArgs(
        val j11Home: String,
        val j14Home: String,
        val appVersion: String,
        val appName: String,
        val appDescription: String,
        val appVendor: String,
        val appMainJar: String,
        val appMainClass: String,
        val imageType: String,
        val buildDir: String,
        val input: String,
        val output: String,
        val appIcon: String,
        val jvmArgs: String,
        val modules: String,
        val platformArgs: List<String> // Script accepts max 5 priperties
) {

    init {
        if (platformArgs.size > 5) {
            throw IllegalArgumentException("Script does not accpet more than 5 arguments!")
        }
    }

    val components: List<String> = listOf(
            j11Home,
            j14Home,
            appVersion,
            appName,
            appDescription,
            appVendor,
            appMainJar,
            appMainClass,
            imageType,
            buildDir,
            input,
            output,
            appIcon,
            jvmArgs,
            modules
    ).plus(platformArgs)
}