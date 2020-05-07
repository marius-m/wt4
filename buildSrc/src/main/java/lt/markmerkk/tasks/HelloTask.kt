package lt.markmerkk.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HelloTask: DefaultTask() {

    @TaskAction
    fun run() {
        println("Hello")
    }

}