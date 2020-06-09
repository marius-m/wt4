package lt.markmerkk.export

import android.databinding.tool.ext.constructorSpec
import lt.markmerkk.export.icons.GenIconMac
import lt.markmerkk.export.icons.GenIconWindows
import lt.markmerkk.export.tasks.BundleTask
import lt.markmerkk.export.tasks.JBundleExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class JBundle: Plugin<Project> {

    override fun apply(project: Project) {
        val extension: JBundleExtension = project.extensions.create(
                EXTENSION_NAME,
                JBundleExtension::class.java,
                project)
//        project.tasks.register(
//                "${NAME_PLUGIN}Export",
//                BundleTask::class.java,
//                extension.appName,
//                extension.versionName,
//                extension.mainClassName
//        ).apply {
//            group = NAME_PLUGIN
//            description = "Creates and exports bundle"
//        }
        project.tasks.register(
                "${NAME_PLUGIN}GenIconMac",
                GenIconMac::class.java
        ) {
            group = NAME_PLUGIN
            description = "Generates icons for MacOSX"
            init(
                    mainIconFilePath = extension.mainIconFilePath,
                    scriptsDirPath = extension.scriptsDirPath
            )
            doFirst {
                debugPrint()
            }
        }
        project.tasks.register("${NAME_PLUGIN}GenIconWin", GenIconWindows::class.java) {
            group = NAME_PLUGIN
            description = "Generates icon for Windows"
            init(
                    mainIconFilePath = extension.mainIconFilePath,
                    scriptsDirPath = extension.scriptsDirPath
            )
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
