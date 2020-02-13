package lt.markmerkk.upgrade

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.LoggerFactory
import rx.Single
import java.lang.IllegalStateException

/**
 * sftp client
 * Lifecycle: [connect] / [disconnect]
 */
class SFTPClient(private val creds: SFTPCreds) {

    private var session: Session? = null
    private var channel: Channel? = null

    fun connect(): Single<ChannelSftp> {
        return Single.defer {
            logger.debug("Connecting to SFTP")
            val ssh = JSch()
            val session = ssh.getSession(
                    creds.username,
                    creds.hostname,
                    creds.port
            ) ?: throw IllegalStateException("Cannot initialize session")
            session.setConfig("StrictHostKeyChecking", "no")
            session.setPassword(creds.pass)
            session.connect()
            val channel = session.openChannel("sftp") ?: throw IllegalStateException("Cannot establish channel")
            channel.connect()
            val sftp = channel as ChannelSftp
            this.session = session
            this.channel = channel
            logger.debug("Connection established")
            Single.just(sftp)
        }
    }

    fun disconnect() {
        logger.debug("Disconnecting from SFTP")
        session?.disconnect()
        channel?.disconnect()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SFTPClient::class.java)!!
    }

}