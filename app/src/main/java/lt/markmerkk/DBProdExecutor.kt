package lt.markmerkk

import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Paths
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import lt.markmerkk.entities.database.DBBaseExecutor
import lt.markmerkk.utils.UserSettingsImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
class DBProdExecutor(
        private val config: Config,
        private val settings: UserSettings
) : DBBaseExecutor() {

    init {
        logger.debug("Applying database migrations")
        if (settings.version <= 0) {
            logger.debug("Found unversioned database! Flushing database with new version.")
            settings.version = Main.VERSION_CODE
        }
        migrate()
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
        val logger = LoggerFactory.getLogger(DBProdExecutor::class.java)!!
    }

}
