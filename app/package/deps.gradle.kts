tasks.create("findDeps", DefaultTask::class.java) {
    group = "Utils"
    description = "Find dependencies for all tasks"
    dependsOn("installDist")
    doLast {
        val dependencyDir = File(project.buildDir, "/install/app/lib")
        val targetFile = File(dependencyDir, "/app-1.8.99.jar")
        if (!dependencyDir.exists() || !dependencyDir.isDirectory) {
            throw java.lang.IllegalStateException("No library path @${dependencyDir.absolutePath}")
        }
        println("Target: $dependencyDir")
        val dependencyFiles: Array<File> = dependencyDir.listFiles() ?: emptyArray()
        val jarFiles: List<File> = dependencyFiles
            .toList()
            .filter { it.isFile && it.extension.endsWith("jar") }
        println("Dependency files: ${jarFiles}")
        val jDepsExec = File(System.getenv("J11_HOME"), "/bin/jdeps")
        if (!jDepsExec.exists()) {
            throw java.lang.IllegalStateException("JDeps does not exist at ${jDepsExec}")
        }
        val javaLibsAsString = dependencyFiles
            .map { "${dependencyDir.absolutePath}/${it.name}" }
            .joinToString(":")
        val depsAsString = listOf(
                jDepsExec.absolutePath,
                "--multi-release",
                "11",
                "--class-path",
                "\"${javaLibsAsString}\"",
                "-R",
                "-summary",
                targetFile.absolutePath
        ).executeAndWait(project.buildDir)
        println(depsAsString)
        val depsPairs: List<Pair<String, String>> = depsAsString.split("\n")
            .filter { it.isNotEmpty() }
            .filter { !it.startsWith("Warning: split package:") }
            .map { newLine ->
                val splitPair = newLine.split(" -> ")
                if (splitPair.size < 2) {
                    throw java.lang.IllegalStateException("Cannot map result: ${newLine}")
                }
                splitPair[0].trim() to splitPair[1]
            }
        // Gather all found j11 module dependencies
        val depPairsFiltered = depsPairs
            .filter { it.second != "not found" } // Ignore not found
            .filter { !it.second.contains(dependencyDir.absolutePath) } // ignore library cross-reference
            .filter { !it.second.startsWith("javafx.") } // ignore jfx dependencies (maily as tornado uses everything possible)
        // Solely app dependencies
        val appPairsFiltered = depsPairs
            .filter { it.first == targetFile.name }
            .filter { it.second != "not found" } // Ignore not found
            .filter { !it.second.contains(dependencyDir.absolutePath) } // ignore library cross-reference
        val moduleSet = depPairsFiltered.plus(appPairsFiltered)
            .map { it.second }
            .toSet()
            .toList()
        val moduleSetAsString = moduleSet.joinToString(",")
        println("Out: $moduleSetAsString")
    }
}

fun List<String>.execute(workingDir: File): Process {
    println("Command: ${this.joinToString(" ")}")
    return ProcessBuilder(this)
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
}

fun List<String>.executeAndWait(workingDir: File): String {
    val process = execute(workingDir)
    return if (process.waitFor() == 0) {
        process.text
    } else {
        throw java.lang.IllegalStateException("Error execute ${process.errorAsString()}")
    }
}

val Process.text: String
    get() = inputStream.bufferedReader().readText()

fun Process.errorAsString(): String {
    return this.errorStream.bufferedReader().use { it.readText() }
}
