package lt.markmerkk.export.tasks

import org.gradle.api.Project

open class JBundleExtension(private val project : Project) {
    var appName: String = ""
    var versionName: String = ""
    var mainClassName: String = ""
    var mainIconFilePath: String = ""
    var scriptsDirPath: String = ""
}