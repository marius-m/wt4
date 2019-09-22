package lt.markmerkk.widgets.versioner

import lt.markmerkk.BuildConfig
import lt.markmerkk.versioner.Changelog
import lt.markmerkk.versioner.VersionProvider
import lt.markmerkk.widgets.network.Api
import okhttp3.ResponseBody
import rx.Single

class VersionProviderImpl(private val api: Api): VersionProvider {

    override fun changelogAsString(): Single<String> {
        return api.changelog()
                .map { it.string() }
    }

    override fun currentVersion(): Changelog.Version {
        return Changelog.versionFrom(BuildConfig.versionName)
    }

}