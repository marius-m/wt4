package lt.markmerkk

import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import lt.markmerkk.entities.database.DBBaseExecutor
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
        migrate()
    }


    override fun database(): String {
        return "test_database.db"
    }

    override fun migrationScriptPath(): URI {
        return Paths.get("src/main/resources/" + "changelog_1.xml").toUri()
    }

    override fun migrationExportPath(): URI {
        try {
            return javaClass.getResource("/").toURI()
        } catch (e: URISyntaxException) {
            throw IllegalStateException("Can't find migration config path")
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(DBTestExecutor::class.java)!!
        val FILE = "test_database.db"
    }

}
