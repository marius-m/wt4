package lt.markmerkk.export.tasks

import org.gradle.api.Project

open class JBundleExtension(private val project : Project) {
    var versionName: String = ""
}