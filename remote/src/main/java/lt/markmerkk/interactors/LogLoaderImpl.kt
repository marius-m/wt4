package lt.markmerkk.interactors

import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile

class LogLoaderImpl(
        private val configPath: String
) : LogLoader {

    override fun loadLastLogs(fileName: String, linesToLoad: Int): String {
        var fileHandler: RandomAccessFile? = null
        try {
            val file = File(configPath + fileName)
            fileHandler = RandomAccessFile(file, "r")
            val sb = StringBuilder()
            var line = 0
            val fileLength = fileHandler.length() - 1
            var filePointer = fileLength
            do {
                fileHandler.seek(filePointer)
                val readByte = fileHandler.readByte().toInt()
                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line += 1
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line += 1
                    }
                }
                if (line >= linesToLoad) {
                    break
                }
                sb.append(readByte.toChar())
                filePointer -= 1
            } while (filePointer != -1L)
            return sb.reverse().toString()
        } catch (e: FileNotFoundException) {
            logger.warn("[WARNING] Cannot find \"$fileName\" in \"$configPath\"")
            return ""
        } catch (e: IOException) {
            logger.warn("[WARNING] Problem reading \"$fileName\"")
            return ""
        } finally {
            try {
                fileHandler?.close()
            } catch (e: Exception) {
                logger.warn("[WARNING] Cant close \"$fileName\"")
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}