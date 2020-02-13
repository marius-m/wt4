package lt.markmerkk.upgrade

import com.jcraft.jsch.ChannelSftp
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.functions.Action1
import java.io.File
import java.io.FileOutputStream

class DownloadEmitter(
        private val fileToDownload: RemoteFileRepository.AppJar,
        private val channelSftp: ChannelSftp
): Action1<Emitter<Double>> {

    override fun call(emitter: Emitter<Double>) {
        val pathAsFile = File.createTempFile("tmp", ".jar")
        val inputStream = channelSftp.get(fileToDownload.filePath)
        val outputStream = FileOutputStream(pathAsFile)
        val data = ByteArray(4096)
        var count: Int = inputStream.read(data)
        var progress: Double = 0.0
        val fileSize = fileToDownload.size
        while (count != -1) {
            outputStream.write(data, 0, count)
            progress += count
            val totalProgress = progress / fileSize
//                logger.info("Progress: " + progress + "/" + fileSize + " >>>> " + totalProgress)
            count = inputStream.read(data)
//                val appFileDownload = RemoteFileRepository.AppFileDownload(
//                        remotePath = fileToDownload.filename,
//                        localPath = pathAsFile.absolutePath,
//                        downloadProgress = totalProgress
//                )
            emitter.onNext(totalProgress)
        }
        outputStream.flush()
        logger.info("Download complete in ${pathAsFile.absolutePath}")
        emitter.onCompleted()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DownloadEmitter::class.java)!!
    }

}