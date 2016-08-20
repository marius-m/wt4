package lt.markmerkk

import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import lt.markmerkk.entities.database.DBBaseExecutor
import lt.markmerkk.entities.jobs.CreateJobIfNeeded
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Test database for various mock sqlite transactions
 */
class DBTestExecutor : DBBaseExecutor() {

    init {
        logger.debug("Applying database migrations")
        try {
            val path = FileSystems.getDefault().getPath(FILE)
            val result = Files.deleteIfExists(path)
            executeOrThrow(CreateJobIfNeeded(SimpleLog::class.java))
            executeOrThrow(CreateJobIfNeeded(LocalIssue::class.java))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun database(): String {
        return "test_database.db"
    }

    override fun migrationScriptPath(): URI? {
        return null
    }

    override fun migrationExportPath(): URI? {
        return null
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DBTestExecutor::class.java)!!
        private val FILE = "test_database.db"
    }

}
