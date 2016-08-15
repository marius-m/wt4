package lt.markmerkk.interactors

import lt.markmerkk.VersionSummary
import rx.subjects.BehaviorSubject

/**
 * @author mariusmerkevicius
 * @since 2016-08-15
 */
interface VersionUpdater {
    val progressSubject: BehaviorSubject<Float>
    var value: VersionSummary

    fun run()
}