package lt.markmerkk.repositories.entities

import java.io.File
import java.util.jar.JarEntry

/**
 * Represents a resource that has a specific type
 * When using in development mode, the resource is read as local file.
 * But when everything is assembled it is packaged in jar and needs different
 * reading mechanism
 */
sealed class ExternalRes(
    val path: String,
    val name: String
) {
    override fun toString(): String {
        return "ExternalRes(path='$path', name='$name')"
    }
}

class ExternalResLocal(
    path: String,
    name: String,
    val file: File
): ExternalRes(path, name) {
    override fun toString(): String {
        return "ExternalResLocal(path='$path', name='$name', file=${file.absolutePath})"
    }
}

class ExternalResJar(
    val jarEntry: JarEntry
): ExternalRes(jarEntry.name, fileNameFromPath(jarEntry.name)) {

    val isDirectory = jarEntry.isDirectory

    companion object {
        fun fileNameFromPath(
            path: String
        ): String {
            return path
                .split("/")
                .last()
        }
    }

    override fun toString(): String {
        return "ExternalResJar(path='$path', name='$name', jarEntry=${jarEntry.name}, isDirectory=$isDirectory)"
    }
}
