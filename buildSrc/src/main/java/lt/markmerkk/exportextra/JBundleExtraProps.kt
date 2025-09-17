package lt.markmerkk.exportextra

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class JBundleExtraProps(
    val appName: String,
    val appType: AppType,
    val appFlavor: String,
    val versionName: String,
    val versionCode: Int,
    val debug: Boolean,
    val systemWide: Boolean,
    val jvmProps: List<String>,
    val sentryDsn: String,
    val gaKey: String,
    val oauth: Boolean,
    val oauthKeyConsumer: String,
    val oauthKeyPrivate: String,
    val oauthHost: String
)

enum class AppType(val rawType: String) {
    DEBUG("debug"),
    BASIC("basic"),
}

data class VersionProps(
        val name: String,
        val flavor: String,
        val code: Int
) {
    companion object {
        fun fromProps(project: Project, flavor: String): VersionProps {
            val versionPropsFile = File(project.rootDir, "version.properties")
            val versionProps = Properties().apply {
                load(FileInputStream(versionPropsFile.absolutePath))
            }
            return VersionProps(
                    name = versionProps.getProperty("version_name"),
                    flavor = flavor,
                    code = versionProps.getProperty("version_code").toInt()
            )
        }
    }
}

data class SentryProps(
    val dsn: String
) {
    companion object {
        fun fromProps(project: Project): SentryProps {
            val propsFile = File(project.rootDir, "sentry.properties")
            val props = Properties().apply {
                load(FileInputStream(propsFile.absolutePath))
            }
            return SentryProps(
                dsn = props.getProperty("dsn").trim()
            )
        }
    }
}
