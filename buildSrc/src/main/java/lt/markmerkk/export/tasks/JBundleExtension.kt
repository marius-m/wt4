package lt.markmerkk.export.tasks

import org.gradle.api.Project

open class JBundleExtension(private val project : Project) {
    var appName: String = ""
    var version: String = ""
    var mainClassName: String = ""
    var mainJarFilePath: String = ""
    var systemWide: Boolean = false
    var jvmProps: List<String> = emptyList()

    var mainIconFilePath: String = ""
    var scriptsDirPath: String = ""
}