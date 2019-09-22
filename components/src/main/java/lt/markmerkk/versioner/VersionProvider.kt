package lt.markmerkk.versioner

import rx.Single

interface VersionProvider {
    /**
     * @return Changelog promise as string
     */
    fun changelogAsString(): Single<String>
}