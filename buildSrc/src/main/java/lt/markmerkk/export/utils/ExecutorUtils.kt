package lt.markmerkk.export.utils

import java.io.File

fun String.execute(
        workingDir: File
): Process {
    val parts = this.split("\\s".toRegex())
    return parts
            .toList()
            .execute(workingDir)
}

fun List<String>.execute(workingDir: File): Process {
    println("Command: ${this.joinToString(" ")}")
    return ProcessBuilder(this)
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
}

val Process.text: String
    get() = inputStream.bufferedReader().readText()

fun Process.errorAsString(): String {
    return this.errorStream
        .bufferedReader()
        .use {
            it.readText()
        }
}
