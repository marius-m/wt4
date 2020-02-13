package lt.markmerkk.upgrade

import com.jcraft.jsch.ChannelSftp
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable
import rx.Single
import java.util.concurrent.TimeUnit

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
        return sftpClient.connect()
                .flatMapObservable {
                    Observable.create(DownloadEmitter(appJar, it), Emitter.BackpressureMode.BUFFER)
                }.throttleLast(200L, TimeUnit.MILLISECONDS)
                .doAfterTerminate { sftpClient.disconnect() }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RemoteFileRepositoryImpl::class.java)!!
    }

}