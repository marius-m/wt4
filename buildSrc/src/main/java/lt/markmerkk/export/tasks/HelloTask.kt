package lt.markmerkk.export.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class HelloTask: DefaultTask() {

    @TaskAction
    fun run() {
        println("Hello")
    }

}