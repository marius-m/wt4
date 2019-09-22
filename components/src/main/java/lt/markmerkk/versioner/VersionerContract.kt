package lt.markmerkk.versioner

interface VersionerContract {

    interface View {
        fun render(
                currentVersion: String,
                availableVersion: String,
                changelogAsString: String
        )
    }

    interface Presenter {
        fun checkVersions(changelog: Changelog)
    }

}