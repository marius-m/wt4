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

    private var subs: Subscription? = null

    fun onAttach() {}
    fun onDetach() {
        subs?.unsubscribe()
    }

    fun check() {
        subs?.unsubscribe()
        subs = versionProvider.changelogAsString()
                .map { Changelog.from(it) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    logger.info("Found remote changelog")
                    listener.onNewVersion(it)
                }, {
                    logger.error("Changelog fetch failure", it)
                })
    }

    interface Listener {
        fun onNewVersion(changelog: Changelog)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}