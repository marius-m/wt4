package lt.markmerkk.entities

import com.vinumeris.updatefx.UpdateSummary

/**
 * @author mariusmerkevicius
 * @since 2016-08-15
 */
class VersionSummaryImpl(
        private val updateSummary: UpdateSummary
) : VersionSummary<UpdateSummary> {
    override fun get(): UpdateSummary = updateSummary
}