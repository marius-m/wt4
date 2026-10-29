package lt.markmerkk

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.After
import org.junit.Before
import org.junit.Test

class DBInteractorLogTest {

    private lateinit var dbDriverProvider: DBDriverProvider
    private lateinit var dbInteractor: DBInteractorLog

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        dbDriverProvider = DBDriverProvider(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY))
        dbInteractor = DBInteractorLog(dbDriverProvider.database, timeProvider)
    }

    @After
    fun tearDown() {
        dbDriverProvider.close()
    }

    @Test
    fun insert_returnsNewIds() {
        // Act
        val id1 = dbInteractor.insert(Mocks.createLog(timeProvider, id = Const.NO_ID))
        val id2 = dbInteractor.insert(Mocks.createLog(timeProvider, id = Const.NO_ID))

        // Assert
        assertThat(id1).isEqualTo(1L)
        assertThat(id2).isEqualTo(2L)
    }

    @Test
    fun insert_findByLocalId() {
        // Assemble
        val log = Mocks.createLog(
                timeProvider,
                id = Const.NO_ID,
                code = "DEV-111",
                comment = "comment",
                systemNote = "system_note",
                author = "author",
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 200,
                        isDeleted = true,
                        isDirty = true,
                        errorMessage = "error",
                        fetchTime = 10L,
                        url = REMOTE_URL_200
                )
        )

        // Act
        val id = dbInteractor.insert(log)
        val result = dbInteractor.findByLocalId(id)!!

        // Assert
        assertThat(result.id).isEqualTo(id)
        assertThat(result.time.startAsRaw).isEqualTo(log.time.startAsRaw)
        assertThat(result.time.endAsRaw).isEqualTo(log.time.endAsRaw)
        assertThat(result.code.code).isEqualTo("DEV-111")
        assertThat(result.comment).isEqualTo("comment")
        assertThat(result.systemNote).isEqualTo("system_note")
        assertThat(result.author).isEqualTo("author")
        val remoteData = result.remoteData!!
        assertThat(remoteData.remoteId).isEqualTo(200L)
        assertThat(remoteData.isDeleted).isTrue()
        assertThat(remoteData.isDirty).isTrue()
        assertThat(remoteData.errorMessage).isEqualTo("error")
        assertThat(remoteData.fetchTime).isEqualTo(10L)
        assertThat(remoteData.url).isEqualTo(REMOTE_URL_200)
    }

    @Test
    fun findByRemoteId() {
        // Assemble
        val id = dbInteractor.insert(logWithRemote(remoteId = 200, url = REMOTE_URL_200))

        // Act
        val result = dbInteractor.findByRemoteId(200)

        // Assert
        assertThat(result!!.id).isEqualTo(id)
        assertThat(dbInteractor.findByRemoteId(300)).isNull()
    }

    @Test
    fun update() {
        // Assemble
        val id = dbInteractor.insert(Mocks.createLog(timeProvider, id = Const.NO_ID, comment = "old"))

        // Act
        val result = dbInteractor.update(Mocks.createLog(timeProvider, id = id, comment = "new"))

        // Assert
        assertThat(result).isEqualTo(id)
        assertThat(dbInteractor.findByLocalId(id)!!.comment).isEqualTo("new")
    }

    @Test
    fun exist() {
        // Assemble
        val id = dbInteractor.insert(logWithRemote(remoteId = 200, url = REMOTE_URL_200))

        // Act + Assert
        assertThat(dbInteractor.existAsLocal(id)).isTrue()
        assertThat(dbInteractor.existAsLocal(id + 1)).isFalse()
        assertThat(dbInteractor.existAsLocal(Const.NO_ID)).isFalse()
        assertThat(dbInteractor.existAsRemote(200)).isTrue()
        assertThat(dbInteractor.existAsRemote(300)).isFalse()
        assertThat(dbInteractor.existAsRemote(Const.NO_ID)).isFalse()
    }

    @Test
    fun deleteByLocalId() {
        // Assemble
        val id = dbInteractor.insert(Mocks.createLog(timeProvider, id = Const.NO_ID))

        // Act
        val resultDelete = dbInteractor.deleteByLocalId(id)
        val resultDeleteAgain = dbInteractor.deleteByLocalId(id)

        // Assert
        assertThat(resultDelete).isEqualTo(id)
        assertThat(resultDeleteAgain).isEqualTo(Const.NO_ID)
        assertThat(dbInteractor.existAsLocal(id)).isFalse()
    }

    @Test
    fun deleteByRemoteId() {
        // Assemble
        val id = dbInteractor.insert(logWithRemote(remoteId = 200, url = REMOTE_URL_200))

        // Act
        val resultDelete = dbInteractor.deleteByRemoteId(200)
        val resultDeleteAgain = dbInteractor.deleteByRemoteId(200)

        // Assert
        assertThat(resultDelete).isEqualTo(id)
        assertThat(resultDeleteAgain).isEqualTo(Const.NO_ID)
        assertThat(dbInteractor.existAsRemote(200)).isFalse()
    }

    @Test
    fun loadWorklogs_rangeIsInclusiveByStart() {
        // Assemble
        val from = LocalDate(2020, 1, 10)
        val to = LocalDate(2020, 1, 11)
        val beforeFrom = from.toDateTime(LocalTime.MIDNIGHT).minusMinutes(1)
        val atFrom = from.toDateTime(LocalTime.MIDNIGHT)
        val atTo = to.toDateTime(LocalTime.MIDNIGHT)
        val afterTo = to.toDateTime(LocalTime.MIDNIGHT).plusMinutes(1)
        listOf(beforeFrom, atFrom, atTo, afterTo)
                .forEachIndexed { index, start ->
                    dbInteractor.insert(
                            Mocks.createLog(
                                    timeProvider,
                                    id = Const.NO_ID,
                                    start = start,
                                    end = start.plusMinutes(10),
                                    comment = "log_$index"
                            )
                    )
                }

        // Act
        val result = dbInteractor.loadWorklogs(from, to)

        // Assert
        assertThat(result.map { it.comment }).containsOnly("log_1", "log_2")
    }

    private fun logWithRemote(remoteId: Long, url: String) = Mocks.createLog(
            timeProvider,
            id = Const.NO_ID,
            remoteData = Mocks.createRemoteData(timeProvider, remoteId = remoteId, url = url)
    )

    companion object {
        private const val REMOTE_URL_200 = "https://jira.example.com/rest/api/2/issue/DEV-111/worklog/200"
    }
}
