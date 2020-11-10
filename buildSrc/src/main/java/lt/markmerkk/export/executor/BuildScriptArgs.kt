package lt.markmerkk.export.executor

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
        val jvmArgs: String
) {
    val components = listOf(
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
            jvmArgs
    )
    val componentsAsString = components.joinToString(" ")
}