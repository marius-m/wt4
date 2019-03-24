package lt.markmerkk

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.database.DBBaseExecutor
import lt.markmerkk.entities.jobs.CreateJobIfNeeded
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Paths

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
class DBProdExecutor(
        private val config: Config,
        private val settings: UserSettings
) : DBBaseExecutor() {

    init {
//        logger.debug("Applying database migrations")
//        if (settings.version <= config.versionCode) {
//            logger.debug("Database older version, adding migrations.")
//            settings.version = config.versionCode
//            migrate()
//        }
        executeOrThrow(CreateJobIfNeeded(SimpleLog::class.java))
    }

    override fun database(): String {
        return config.cfgPath + "wt4_1.db"
    }

    override fun migrationScriptPath(): URI {
        try {
            return javaClass.getResource("/changelog_1.xml").toURI()
        } catch (e: URISyntaxException) {
            throw IllegalStateException("Can't find migration config path")
        }
    }

    override fun migrationExportPath(): URI {
        return Paths.get(config.cfgPath).toUri()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DBProdExecutor::class.java)!!
    }

}
