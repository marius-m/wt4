package lt.markmerkk.export.utils

import org.apache.commons.io.IOUtils
import java.io.File
import java.io.StringWriter

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
    val writer = StringWriter()
    IOUtils.copy(this.errorStream, writer, Charsets.UTF_8.toString())
    return writer.toString()
}
