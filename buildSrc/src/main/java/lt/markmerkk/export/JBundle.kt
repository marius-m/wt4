package lt.markmerkk.export

import org.gradle.api.Plugin
import org.gradle.api.Project
import lt.markmerkk.export.tasks.HelloTask
import lt.markmerkk.export.tasks.JBundleExtension
import lt.markmerkk.export.tasks.GenIcons
import lt.markmerkk.export.tasks.GenIconMac
import lt.markmerkk.export.tasks.GenIconWindows
import org.gradle.api.DefaultTask

class JBundle: Plugin<Project> {

    override fun apply(project: Project) {
//        val pushToOrigin = project.getPropertyBool("doRelease")
        val extension: JBundleExtension = project.extensions.create(
                EXTENSION_NAME,
                JBundleExtension::class.java,
                project)
        project.tasks.register("${NAME_PLUGIN}Export", HelloTask::class.java) {
            group = NAME_PLUGIN
            description = "Creates and exports bundle"
        }
        project.tasks.register("${NAME_PLUGIN}GenIconMac", GenIconMac::class.java) {
            group = NAME_PLUGIN
            description = "Generates icon for MacOSX"
        }
        project.tasks.register("${NAME_PLUGIN}GenIconWin", GenIconWindows::class.java) {
            group = NAME_PLUGIN
            description = "Generates icon for Windows"
        }
        project.tasks.register("${NAME_PLUGIN}GenIcons", DefaultTask::class.java) {
            group = NAME_PLUGIN
            description = "Generates icons for app export"
            val deps = listOf("${NAME_PLUGIN}GenIconMac", "${NAME_PLUGIN}GenIconWin")
            setDependsOn(deps)
        }
    }


    companion object {
        private const val NAME_PLUGIN = "jBundle"
        private const val EXTENSION_NAME = "jbundle"
    }

}
