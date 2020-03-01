package lt.markmerkk.export

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import lt.markmerkk.FileInteractor
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log
import lt.markmerkk.export.entities.ExportLogResponse
import lt.markmerkk.export.entities.ImportLogResponse
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Responsible for exporting worklogs
 */
class WorklogExporter(
        private val gson: Gson,
        private val fileInteractor: FileInteractor,
        private val timeProvider: TimeProvider
) {

    fun export(worklogs: List<Log>): String {
        val exportWorklogs = worklogs
                .map { ExportLogResponse.fromLog(it) }
        return gson.toJson(exportWorklogs)
    }

    /**
     * Exports worklogs to file
     * @return true if export was success, false otherwise
     */
    fun exportToFile(worklogs: List<Log>): Boolean {
        try {
            val saveFile = fileInteractor.saveFile()
            if (saveFile != null) {
                val worklogsAsString = export(worklogs)
                saveFile.outputStream()
                        .use { IOUtils.write(worklogsAsString, it, Charsets.UTF_8) }
                return true
            }
        } catch (e: Exception) {
            logger.error("Error exporting worklogs", e)
            return false
        }
        return false
    }

    fun importFromFile(): List<Log> {
        val selectFile = fileInteractor.loadFile()
        if (selectFile != null) {
            val fileAsString = FileUtils
                    .readFileToString(selectFile, Charsets.UTF_8)
            try {
                return gson.fromJson<List<ImportLogResponse>>(
                        fileAsString,
                        object : TypeToken<List<ImportLogResponse>>(){}.type
                ).map {
                    it.toLog(timeProvider)
                }
            } catch (e: JsonParseException) {
                logger.error("Cannot parse json", e)
            } catch (e: JsonSyntaxException) {
                logger.error("Cannot parse json", e)
            }
        }
        return emptyList()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WorklogExporter::class.java)!!
    }

}