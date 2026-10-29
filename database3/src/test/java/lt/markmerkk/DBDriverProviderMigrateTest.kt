package lt.markmerkk

import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class DBDriverProviderMigrateTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var dbDir: File
    private var dbDriverProvider: DBDriverProvider? = null

    @Before
    fun setUp() {
        dbDir = tempFolder.newFolder()
    }

    @After
    fun tearDown() {
        dbDriverProvider?.close()
    }

    @Test
    fun freshDatabase() {
        // Act
        openDatabase()

        // Assert
        withConnection { conn ->
            assertThat(userVersion(conn)).isEqualTo(10)
            assertThat(tables(conn)).containsOnly("ticket", "worklog", "ticket_status", "ticket_use_history")
        }
    }

    @Test
    fun fromVersion1() {
        // Assemble
        withConnection { conn ->
            executeScript(conn, "legacy_schema_v1.sql")
            conn.createStatement().execute("INSERT INTO ticket(code, description) VALUES ('WT-1', 'desc')")
            conn.createStatement().execute("PRAGMA user_version = 1")
        }

        // Act
        openDatabase()

        // Assert
        withConnection { conn ->
            assertThat(userVersion(conn)).isEqualTo(10)
            assertThat(tables(conn)).containsOnly("ticket", "worklog", "ticket_status", "ticket_use_history")
            assertThat(columns(conn, "worklog")).contains("system_note", "author")
            assertThat(columns(conn, "ticket")).contains("parent_code", "status", "assignee", "reporter", "is_watching")
            assertThat(columns(conn, "ticket_status")).contains("enabled")
            val ticketCodes = conn.createStatement().executeQuery("SELECT code FROM ticket")
            assertThat(ticketCodes.next()).isTrue()
            assertThat(ticketCodes.getString("code")).isEqualTo("WT-1")
        }
    }

    @Test
    fun fromVersion10_noChanges() {
        // Assemble
        withConnection { conn ->
            executeScript(conn, "legacy_schema_v10.sql")
            conn.createStatement().execute("INSERT INTO worklog(code, comment) VALUES ('WT-1', 'comment')")
            conn.createStatement().execute("PRAGMA user_version = 10")
        }
        val schemaBefore = withConnection { conn -> schemaSql(conn) }

        // Act
        openDatabase()

        // Assert
        withConnection { conn ->
            assertThat(userVersion(conn)).isEqualTo(10)
            assertThat(schemaSql(conn)).isEqualTo(schemaBefore)
            val worklogCount = conn.createStatement().executeQuery("SELECT COUNT(*) FROM worklog")
            worklogCount.next()
            assertThat(worklogCount.getInt(1)).isEqualTo(1)
        }
    }

    private fun openDatabase() {
        val provider = DBDriverProvider(databaseName = DB_NAME, databasePath = dbDir.absolutePath)
        provider.database // triggers create / migrate
        dbDriverProvider = provider
    }

    private fun <T> withConnection(block: (Connection) -> T): T {
        return DriverManager.getConnection("jdbc:sqlite:${File(dbDir, DB_NAME).absolutePath}")
                .use(block)
    }

    private fun executeScript(conn: Connection, resourceName: String) {
        val script = javaClass.classLoader.getResource(resourceName)!!.readText()
        script.split(";")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { conn.createStatement().execute(it) }
    }

    private fun userVersion(conn: Connection): Int {
        val rs = conn.createStatement().executeQuery("PRAGMA user_version")
        rs.next()
        return rs.getInt(1)
    }

    private fun tables(conn: Connection): List<String> {
        val rs = conn.createStatement()
                .executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%'")
        val names = mutableListOf<String>()
        while (rs.next()) {
            names.add(rs.getString(1))
        }
        return names
    }

    private fun columns(conn: Connection, table: String): List<String> {
        val rs = conn.createStatement().executeQuery("PRAGMA table_info(`$table`)")
        val names = mutableListOf<String>()
        while (rs.next()) {
            names.add(rs.getString("name"))
        }
        return names
    }

    private fun schemaSql(conn: Connection): List<String> {
        val rs = conn.createStatement().executeQuery("SELECT sql FROM sqlite_master ORDER BY name")
        val sqls = mutableListOf<String>()
        while (rs.next()) {
            sqls.add(rs.getString(1) ?: "")
        }
        return sqls
    }

    companion object {
        private const val DB_NAME = "wt4_2.db"
    }
}
