package lt.markmerkk

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import lt.markmerkk.db.WT4Database
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WT4DatabaseCreateTest {

    private lateinit var driver: JdbcSqliteDriver

    @Before
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    }

    @After
    fun tearDown() {
        driver.close()
    }

    @Test
    fun createsAllTables() {
        // Act
        WT4Database.Schema.create(driver)

        // Assert
        val tables = driver.executeQuery(
                identifier = null,
                sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%'",
                mapper = { cursor ->
                    val names = mutableListOf<String>()
                    while (cursor.next().value) {
                        names.add(cursor.getString(0)!!)
                    }
                    QueryResult.Value(names)
                },
                parameters = 0
        ).value
        assertThat(tables).containsOnly("ticket", "worklog", "ticket_status", "ticket_use_history")
    }
}
