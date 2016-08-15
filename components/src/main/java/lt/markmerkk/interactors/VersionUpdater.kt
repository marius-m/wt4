package lt.markmerkk.interactors

import lt.markmerkk.VersionSummary

/**
 * @author mariusmerkevicius
 * @since 2016-08-15
 */
interface VersionUpdater {
    var value: VersionSummary

    fun run()
}