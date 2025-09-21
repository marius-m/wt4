package lt.markmerkk.export.executor

import java.lang.IllegalArgumentException

/**
 * Whenever adding new property, need to list it out on [components] property
 */
data class BuildScriptArgs(
    val javaVersion: String,
    val j17Home: String,
    val jmodsHome: String,
    val appVersion: String,
    val appName: String,
    val appDescription: String,
    val appVendor: String,
    val appMainJar: String,
    val appMainClass: String,
    val imageType: String,
    val buildDir: String,
    val inputLibsDir: String,
    val input: String,
    val output: String,
    val appIcon: String,
    val jvmArgs: String,
    val platformArgs: List<String> // Script accepts max 5 priperties
) {

    init {
        if (platformArgs.size > 5) {
            throw IllegalArgumentException("Script does not accept more than 5 arguments!")
        }
    }

    val components: List<String> = listOf(
        javaVersion,
        j17Home,
        jmodsHome,
        appVersion,
        appName,
        appDescription,
        appVendor,
        appMainJar,
        appMainClass,
        imageType,
        buildDir,
        inputLibsDir,
        input,
        output,
        appIcon,
        jvmArgs
    ).plus(platformArgs)
}