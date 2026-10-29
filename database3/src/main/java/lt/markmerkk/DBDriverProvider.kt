package lt.markmerkk

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import lt.markmerkk.db.WT4Database
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Opens the SQLite database and brings its schema up to [WT4Database.Schema.version]
 * The schema version is tracked with `PRAGMA user_version`
 */
class DBDriverProvider(
        private val driver: SqlDriver
) {

    constructor(
            databaseName: String,
            databasePath: String
    ) : this(JdbcSqliteDriver("jdbc:sqlite:$databasePath${File.separator}$databaseName"))

    val database: WT4Database by lazy {
        val database = WT4Database(driver)
        database.transaction { migrate() }
        database
    }

    fun close() {
        driver.close()
    }

    private fun migrate() {
        val schema = WT4Database.Schema
        val currentVersion = userVersion()
        logger.debug("Current db version - $currentVersion, schema version - ${schema.version}")
        when {
            currentVersion == 0L -> {
                logger.debug("Creating new database")
                schema.create(driver)
                renewUserVersion(schema.version)
            }
            currentVersion < schema.version -> {
                logger.debug("Migrating from $currentVersion .. ${schema.version}")
                schema.migrate(driver, currentVersion, schema.version)
                renewUserVersion(schema.version)
            }
        }
    }

    private fun userVersion(): Long {
        return driver.executeQuery(
                identifier = null,
                sql = "PRAGMA user_version;",
                mapper = { cursor ->
                    cursor.next()
                    QueryResult.Value(cursor.getLong(0) ?: 0L)
                },
                parameters = 0
        ).value
    }

    private fun renewUserVersion(newVersion: Long) {
        driver.execute(identifier = null, sql = "PRAGMA user_version = $newVersion;", parameters = 0)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}
