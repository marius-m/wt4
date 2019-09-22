package lt.markmerkk.widgets.versioner

import lt.markmerkk.versioner.VersionProvider
import lt.markmerkk.widgets.network.Api
import okhttp3.ResponseBody
import rx.Single

class VersionProviderImpl(private val api: Api): VersionProvider {
    override fun changelogAsString(): Single<String> {
        return api.changelog()
                .map { it.string() }
    }
}