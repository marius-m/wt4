package lt.markmerkk.versioner

import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription

/**
 * Responsible for loading changelog
 * Lifecycle: [onAttach], [onDetach]
 */
class ChangelogLoader(
        private val listener: Listener,
        private val versionProvider: VersionProvider,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subCheck: Subscription? = null
    private var subLoad: Subscription? = null

    fun onAttach() {}
    fun onDetach() {
        subCheck?.unsubscribe()
        subLoad?.unsubscribe()
    }

    fun load() {
        subLoad?.unsubscribe()
        subLoad = versionProvider.changelogAsString()
                .map { Changelog.from(it) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    logger.info("Found remote changelog")
                    listener.onChangelog(it)
                }, {
                    logger.error("Changelog fetch failure", it)
                })
    }

    fun check() {
        subCheck?.unsubscribe()
        subCheck = versionProvider.changelogAsString()
                .map { Changelog.from(it) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    logger.info("Found remote changelog")
                    val remoteVersion = it.version
                    val currentVersion = versionProvider.currentVersion()
                    if (remoteVersion > currentVersion) {
                        listener.onNewVersion(it)
                    }
                }, {
                    logger.error("Changelog fetch failure", it)
                })
    }

    interface Listener {
        fun onChangelog(changelog: Changelog)
        fun onNewVersion(changelog: Changelog)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}