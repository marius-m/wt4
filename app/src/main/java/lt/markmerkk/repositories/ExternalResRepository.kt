package lt.markmerkk.repositories

import lt.markmerkk.repositories.entities.ExternalRes
import lt.markmerkk.repositories.entities.ExternalResJar
import lt.markmerkk.repositories.entities.ExternalResLocal
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import rx.Single
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException
import java.util.jar.JarFile

class ExternalResRepository {

    fun readExternalResourceAsString(extResource: ExternalRes): Single<String> {
        return Single.defer {
            val content = when (extResource) {
                is ExternalResJar -> {
                    jarFileToString(extResource.jarEntry.name)
                }
                is ExternalResLocal -> {
                    FileUtils.readFileToString(extResource.file, Charsets.UTF_8.toString())
                }
            }
            Single.just(content)
        }
    }

    fun readExternalResourceAsTmpFile(extResource: ExternalRes): Single<File> {
        return Single.defer {
            val tmpFile = File.createTempFile("tmp", ".data")
            val outFile = when (extResource) {
                is ExternalResJar -> {
                    jarToFile(tmpFile, extResource.jarEntry.name)
                }
                is ExternalResLocal -> {
                    extResource.file
                }
            }
            Single.just(outFile)
        }
    }

    fun externalResources(rootPath: String): Single<List<ExternalRes>> {
        return Single.defer {
            val jarFile = File(javaClass.protectionDomain.codeSource.location.path)
            if (jarFile.isFile) {
                readFromJarResource(rootPath, jarFile)
            } else {
                readFromLocalResource(rootPath)
            }
        }
    }

    private fun readFromLocalResource(
        rootPath: String
    ): Single<List<ExternalRes>> {
        return Single.defer {
            val url = ExternalResRepository::class.java.getResource("${File.separator}$rootPath")
            if (url == null) Single.error<List<ExternalRes>>(IllegalStateException("Cannot figure URL"))
            try {
                val files: List<File> = File(url.toURI())
                    .listFiles()
                    ?.toList() ?: emptyList()
                val res = files
                    .mapNotNull {
                        try {
                            ExternalResLocal(
                                path = rootPath,
                                name = it.name,
                                file = it
                            )
                        } catch (e: IOException) {
                            l.warn("Cannot read ${it.name}", e)
                            null
                        }
                    }
                Single.just(res)
            } catch (ex: URISyntaxException) {
                ex.printStackTrace()
                Single.error(IllegalArgumentException("Error reading local resource"))
            }
        }
    }

    private fun readFromJarResource(
        rootPath: String,
        jarFile: File
    ): Single<List<ExternalRes>> {
        return Single.defer {
            val jar = JarFile(jarFile)
            val entries = jar.entries().toList()
            val jarEntries = entries
                .filter {
                    it.name.startsWith("$rootPath/")
                }
                .mapNotNull {
                    try {
                        ExternalResJar(jarEntry = it)
                    } catch (e: IOException) {
                        l.warn("Cannot read ${it.name}", e)
                        null
                    }
                }.filter { !it.isDirectory }
            jar.close()
            Single.just(jarEntries)
        }
    }

    @Throws(IOException::class)
    private fun jarFileToString(inputPath: String): String {
        val fileIs = javaClass.classLoader.getResourceAsStream(inputPath)
        return IOUtils.toString(fileIs, "UTF-8")
    }

    @Throws(IOException::class)
    private fun jarToFile(inputFile: File, inputPath: String): File {
        val fileIs = javaClass.classLoader.getResourceAsStream(inputPath)
        FileOutputStream(inputFile).use {
            IOUtils.copy(fileIs, it)
        }
        return inputFile
    }

    companion object {
        val l = LoggerFactory.getLogger(ExternalResRepository::class.java)!!
    }

}