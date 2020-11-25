package lt.markmerkk.export.tasks

import lt.markmerkk.export.executor.JDepsScriptJ11Unix
import lt.markmerkk.export.executor.JBundlerScriptProvider
import org.gradle.api.tasks.Exec
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.IllegalStateException

open class DepsTask: Exec() {

    private lateinit var scriptProvider: JBundlerScriptProvider

    fun init(
            mainJarFilePath: String,
            moduleOutputPath: String
    ) {
        val moduleOutputFile: File = File(moduleOutputPath)
        val stdOut = ByteArrayOutputStream()
        val dependencyDir = File(project.buildDir, "/install/app/lib")
        val j11HomePath: String = System.getenv("J11_HOME") ?: ""
        val targetFileName = File(mainJarFilePath).name

        scriptProvider = when (OsType.get()) {
            OsType.UNKNOWN -> throw IllegalStateException("Unsupported OS type")
            OsType.LINUX, OsType.MAC -> {
                JDepsScriptJ11Unix(
                        project = project,
                        jdk11HomeDir = j11HomePath,
                        targetFileName = targetFileName
                )
            }
            OsType.WINDOWS -> {
                throw UnsupportedOperationException()
                //JBundlerScriptJ11Win(project, bundleResource)
            }
        }

        workingDir = project.projectDir
        setStandardOutput(stdOut)

        doFirst {
            val scriptArgs = scriptProvider.scriptCommand()
            setCommandLine(*scriptArgs.toTypedArray())
        }

        doLast {
            val depsAsString = stdOut.toString(Charsets.UTF_8)
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
//            .filter { !it.second.startsWith("javafx.") } // ignore jfx dependencies (maily as tornado uses everything possible)

            // Solely app dependencies
            val appPairsFiltered = depsPairs
                    .filter { it.first == mainJarFilePath }
                    .filter { it.second != "not found" } // Ignore not found
                    .filter { !it.second.contains(dependencyDir.absolutePath) } // ignore library cross-reference
            val moduleSet = depPairsFiltered.plus(appPairsFiltered)
                    .map { it.second }
                    .toSet()
                    .toList()
            val moduleSetAsString = moduleSet.joinToString(",")
            moduleOutputFile.writeText(moduleSetAsString, Charsets.UTF_8)
        }

    }

}

