package lt.markmerkk

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TicketUseHistory
import org.joda.time.DateTime

object Mocks {

    fun createUser(
            name: String = "valid_name",
            displayName: String = "valid_display_name",
            email: String = "valid_email",
            accountId: String = "account_id"
    ): JiraUser {
        return JiraUser(
                name = name,
                displayName = displayName,
                email = email,
        )
    }

    fun createTicket(
            id: Long = Const.NO_ID,
            code: String = "TTS-123",
            description: String = "valid_descriotion",
            parentId: Long = -1,
            parentCode: String = "",
            status: String = "",
            assigneeName: String = "",
            reporterName: String = "",
            isWatching: Boolean = false,
            remoteData: RemoteData? = null
    ): Ticket {
        return Ticket(
                id = id,
                code = TicketCode.new(code),
                description = description,
                parentId = parentId,
                status = status,
                assigneeName = assigneeName,
                reporterName = reporterName,
                isWatching = isWatching,
                parentCode = TicketCode.new(parentCode),
                remoteData = remoteData
        )
    }

    fun createBasicLog(
            timeProvider: TimeProvider,
            localId: Long = 1L
    ): Log {
        val now = timeProvider.nowMillis()
        return createLog(
                timeProvider = timeProvider,
                id = localId
        )
    }

    fun createBasicLogRemote(
        timeProvider: TimeProvider,
        localId: Long = 1L,
        remoteId: Long = 2L,
        start: DateTime = timeProvider.now(),
        end: DateTime = timeProvider.now(),
        code: String = "DEV-123",
        errorMessage: String = "",
    ): Log {
        val now = timeProvider.nowMillis()
        return createLog(
            timeProvider = timeProvider,
            id = localId,
            start = start,
            end = end,
            code = code,
            remoteData = createRemoteData(
                timeProvider,
                remoteId = remoteId,
                errorMessage = errorMessage,
            )
        )
    }

    fun createLog(
            timeProvider: TimeProvider,
            id: Long = 1L,
            start: DateTime = timeProvider.now(),
            end: DateTime = timeProvider.now().plusMinutes(10),
            code: String = "DEV-123",
            comment: String = "valid_comment",
            systemNote: String = "",
            author: String = "author",
            remoteData: RemoteData? = null
    ): Log {
        val now = timeProvider.nowMillis()
        return Log.createAsTestable(
                timeProvider = timeProvider,
                id = id,
                start = start,
                end = end,
                code = code,
                comment = comment,
                systemNote = systemNote,
                author = author,
                remoteData = remoteData
        )
    }

    fun createRemoteData(
            timeProvider: TimeProvider,
            remoteId: Long = 1L,
            isDeleted: Boolean = false,
            isDirty: Boolean = false,
            errorMessage: String = "",
            fetchTime: Long = timeProvider.nowMillis(),
            url: String = ""
    ): RemoteData {
        val now = timeProvider.nowMillis()
        return RemoteData(
                remoteId = remoteId,
                isDeleted = isDeleted,
                isDirty = isDirty,
                errorMessage = errorMessage,
                fetchTime = fetchTime,
                url = url
        )
    }

    fun createJiraBasicCreds(
            hostname: String = "valid_host",
            username: String = "valid_user",
            password: String = "valid_pass"
    ): JiraBasicCreds {
        return JiraBasicCreds(
                host = hostname,
                username = username,
                password = password
        )
    }

    fun createJiraOAuthPreset(
            host: String = "hostname",
            privateKey: String = "private_key",
            consumerKey: String = "consumer_key"
    ): JiraOAuthPreset {
        return JiraOAuthPreset(
                host = host,
                privateKey = privateKey,
                consumerKey = consumerKey

        )
    }

    fun createJiraOAuthCreds(
            tokenSecret: String = "token_secret",
            accessKey: String = "access_key"
    ): JiraOAuthCreds {
        return JiraOAuthCreds(
                tokenSecret = tokenSecret,
                accessKey = accessKey
        )
    }

    fun createJiraUserEmpty(): JiraUser {
        return createJiraUser(name = "", displayName = "", email = "")
    }

    fun createJiraUser(
            name: String = "name",
            displayName: String = "display_name",
            email: String = "email",
    ): JiraUser {
        return JiraUser(
                name = name,
                displayName = displayName,
                email = email,
        )
    }

    fun createTicketUseHistory(
            timeProvider: TimeProvider,
            code: String = "DEV-111",
            description: String = "",
            lastUsed: DateTime = timeProvider.now()
    ): TicketUseHistory {
        return TicketUseHistory(
                TicketCode.new(code),
                description,
                lastUsed
        )
    }
}