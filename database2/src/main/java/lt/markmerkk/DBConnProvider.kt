package lt.markmerkk

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class DBConnProvider(
        private val databaseName: String,
        private val databasePath: String
) {

    fun exist(): Boolean = File("$databasePath$databaseName").exists()

    fun connect(): Connection {
        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection("jdbc:sqlite:$databasePath$databaseName")
    }
}