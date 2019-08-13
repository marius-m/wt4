package lt.markmerkk

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class DBConnProvider(
        private val databaseName: String,
        private val databasePath: String
) {

    val dsl: DSLContext = DSL.using(connect(), SQLDialect.SQLITE)

    fun exist(): Boolean = File("$databasePath$databaseName").exists()

    fun connect(): Connection {
        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection("jdbc:sqlite:$databasePath$databaseName")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}