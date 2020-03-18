package lt.markmerkk.upgrade

import org.slf4j.LoggerFactory

class AppDownloader {

    fun onAttach() {}

    fun onDetach() {}

    fun downloadApp() {
        val localJarPath = RemoteFileRepository::class.java
                .protectionDomain
                .codeSource
                .location
                .path
        logger.debug("Local path: $localJarPath")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AppDownloader::class.java)!!
    }

}