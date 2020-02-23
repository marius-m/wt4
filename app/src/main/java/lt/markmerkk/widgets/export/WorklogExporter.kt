package lt.markmerkk.widgets.export

import com.google.gson.Gson
import lt.markmerkk.FileInteractor
import lt.markmerkk.entities.Log
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Responsible for exporting worklogs
 */
class WorklogExporter(
        private val gson: Gson,
        private val fileInteractor: FileInteractor
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
            val saveDir = fileInteractor.saveDirectory()
            if (saveDir != null) {
                val saveFile = File(saveDir, "worklogs.json")
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

    companion object {
        private val logger = LoggerFactory.getLogger(WorklogExporter::class.java)!!
    }

}