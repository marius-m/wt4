package lt.markmerkk

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.slf4j.LoggerFactory
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


class DBConnProvider(
        private val databaseName: String,
        private val databasePath: String
) {

    private val dbFilePath = "$databasePath${File.separator}$databaseName"
    private val dbFile = File(dbFilePath)
    private val sqliteDataSource = SQLiteDataSource()
    private val configuration = DefaultConfiguration()
            .set(SQLDialect.SQLITE)
            .set(DataSourceConnectionProvider(sqliteDataSource))
            .set(connect())
    val dsl: DSLContext = DSL.using(configuration)

    fun exist(): Boolean = dbFile.exists()

    fun connect(): Connection {
        Class.forName("org.sqlite.JDBC")
        val jdbcPath = "jdbc:sqlite:${dbFilePath}"
        return DriverManager.getConnection(jdbcPath)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}