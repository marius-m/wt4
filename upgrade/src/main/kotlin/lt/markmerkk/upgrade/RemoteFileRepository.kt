package lt.markmerkk.upgrade

import rx.Observable
import rx.Single

interface RemoteFileRepository {

    /**
     * Lists out available files
     */
    fun listAvailableFiles(): Single<List<AppJar>>

    /**
     * File download as stream
     */
    fun download(lsEntry: AppJar): Observable<Double>

    data class AppJar(
            val filePath: String,
            val size: Long
    )

    data class AppFileDownload(
            val remotePath: String,
            val localPath: String,
            val downloadProgress: Double
    )

}