package lt.markmerkk.export.tasks

import org.gradle.api.Project

open class JBundleExtension(private val project : Project) {
    var appName: String = ""
    var version: String = ""
    var mainClassName: String = ""
    var mainJarFilePath: String = ""
    var systemWide: Boolean = false

    var mainIconFilePath: String = ""
    var scriptsDirPath: String = ""
}