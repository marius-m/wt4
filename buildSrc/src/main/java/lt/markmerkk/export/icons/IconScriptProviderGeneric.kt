package lt.markmerkk.export.icons

import org.gradle.api.Project
import java.io.File

/**
 * Generic script that would generate icons
 * Scripts are passed in from outside
 */
class IconScriptProviderGeneric(
        private val project: Project,
        private val mainIconFilePath: String,
        private val scriptsDirPath: String,
        private val scriptFileName: String,
        private val destinationDirName: String
): IconScriptProvider {

    private val iconFilePath = File(mainIconFilePath)
    private val scriptFilePath = File(scriptsDirPath, "/${scriptFileName}")
    private val destinationDir = File("${iconFilePath.parent}/${destinationDirName}")

    init {
        assert(iconFilePath.exists() && iconFilePath.isFile) {
            throw IllegalArgumentException("No icon at ${iconFilePath.absolutePath}")
        }
        assert(scriptFilePath.exists() && scriptFilePath.isFile) {
            throw IllegalArgumentException("No script for exporting icons at ${scriptFilePath.absolutePath}")
        }
    }

    override fun debugPrint() {
        println("Using icon at ${iconFilePath.absolutePath}")
        println("Using script at ${scriptFilePath.absolutePath}")
        println("Generating icon to ${destinationDir.absolutePath}")
    }

    override fun scriptCommand(): List<String> {
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        return listOf(
                "sh",
                scriptFilePath.absolutePath,
                iconFilePath.absolutePath,
                destinationDir.absolutePath,
                iconFilePath.nameWithoutExtension
        )
    }

}