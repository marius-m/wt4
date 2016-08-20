package lt.markmerkk.interactors

import lt.markmerkk.entities.VersionSummary
import rx.subjects.BehaviorSubject

/**
 * @author mariusmerkevicius
 * @since 2016-08-15
 */
interface VersionUpdater<T> {
    val progressSubject: BehaviorSubject<Float>
    var value: VersionSummary<T>?

    fun run()
}