import lt.markmerkk.tasks.HelloTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class WT4ReleasePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val pushToOrigin = getPropertyBool(project, "doRelease")
//        val extension = project.extensions.create(
//                EXTENSION_NAME,
//                AdformPluginExtension::class.java,
//                project)
        project.tasks.register("wt4Export", HelloTask::class.java) {
            group = "Adform"
            description = "Exports library to output dir"
        }
    }

    private fun getPropertyBool(
            project: Project,
            key: String
    ): Boolean {
        return if (project.properties.containsKey(key)) {
            project.properties[key]!!.toString()
                    .toBoolean()
        } else {
            false
        }
    }

    companion object {
        private const val EXTENSION_NAME = "wt4ReleasePlugin"
    }

}
