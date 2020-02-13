package lt.markmerkk.upgrade

import com.jcraft.jsch.ChannelSftp
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable
import rx.Single

class RemoteFileRepositoryImpl(
        private val sftpClient: SFTPClient
) : RemoteFileRepository {

    /**
     * Lists out available files
     */
    override fun listAvailableFiles(): Single<List<RemoteFileRepository.AppJar>> {
        return sftpClient.connect()
                .flatMap { sftp ->
                    val files = sftp.ls("uploads")
                            .map { it as ChannelSftp.LsEntry }
                            .filter { !it.attrs.isDir }
                            .map {
                                RemoteFileRepository.AppJar(
                                        filePath = "uploads/${it.filename}",
                                        size = it.attrs.size
                                )
                            }
                            .toList()
                    logger.info("Files: $files")
                    Single.just(files)
                }.doAfterTerminate { sftpClient.disconnect() }
    }

    override fun download(appJar: RemoteFileRepository.AppJar): Observable<Double> {
        TODO()
//        return Observable
//                .create(DownloadEmitter(appJar, sftpCreds), Emitter.BackpressureMode.BUFFER)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RemoteFileRepositoryImpl::class.java)!!
    }

}