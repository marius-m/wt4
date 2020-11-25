package lt.markmerkk.export

import lt.markmerkk.export.icons.GenIconMac
import lt.markmerkk.export.icons.GenIconWindows
import lt.markmerkk.export.tasks.BundleTask
import lt.markmerkk.export.tasks.DepsTask
import lt.markmerkk.export.tasks.JBundleExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class JBundlePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val extension: JBundleExtension = project.extensions.create(
                EXTENSION_NAME,
                JBundleExtension::class.java,
                project)
        val moduleOutputFile = File(project.buildDir, "module-info.txt")
        project.tasks.register(
                "${NAME_PLUGIN}Create",
                BundleTask::class.java
        ) {
            group = NAME_PLUGIN
            description = "Creates and exports bundle"
            init(
                    appName = extension.appName,
                    versionName = extension.version,
                    systemWide = extension.systemWide,
                    mainJarFilePath = extension.mainJarFilePath,
                    mainClassName = extension.mainClassName,
                    mainIconFilePath = extension.mainIconFilePath,
                    jvmProps = extension.jvmProps,
                    scriptsDirPath = extension.scriptsDirPath,
                    moduleOutputPath = moduleOutputFile.absolutePath
            )
            setDependsOn(listOf(":${project.name}:build"))
        }
        project.tasks.register(
                "${NAME_PLUGIN}Deps",
                DepsTask::class.java
        ) {
            group = NAME_PLUGIN
            description = "Creates and exports bundle"
            init(
                    mainJarFilePath = extension.mainJarFilePath,
                    moduleOutputPath = moduleOutputFile.absolutePath
            )
            setDependsOn(listOf(":${project.name}:installDist"))
        }
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
