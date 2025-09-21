package lt.markmerkk.exportextra

import lt.markmerkk.export.tasks.OsType
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*

object JBundleExtraPropsFactory {

    const val APP_NAME = "WT4"
    const val APP_FLAVOR_BASIC = "basic"

    private val defaultJvmProps = listOf(
        "-Xms128m",
        "-Xmx300m",
        "-XX:+UseG1GC",
        "-splash:\$APPDIR${File.separator}resources${File.separator}splash.png"
    )

    object Debug {
        fun asBasic(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, APP_FLAVOR_BASIC)
            val sentryProps = SentryProps.fromProps(project)
            return JBundleExtraProps(
                appName = "${APP_NAME}-${appType.rawType}",
                appType = appType,
                appFlavor = versionProps.flavor,
                versionName = versionProps.name,
                versionCode = versionProps.code,
                debug = true,
                systemWide = false,
                jvmProps = defaultJvmProps.plus(
                    listOf(
                        "-DWT_APP_PATH=wt4_debug",
                        "-DWT_APP_FLAVOR=${versionProps.flavor}"
                    )
                ),
                sentryDsn = sentryProps.dsn,
                gaKey = "test",
            )
        }

    }

    object Release {
        private fun asBasic(
            appType: AppType,
            project: Project,
            systemWide: Boolean,
            j17HomeOverride: String? = null,
            jmodsHomeOverride: String? = null,
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_BASIC)
            val sentryProps = SentryProps.fromProps(project)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                appName = "${APP_NAME}-${appType.rawType}",
                appType = appType,
                appFlavor = versionProps.flavor,
                versionName = versionProps.name,
                versionCode = versionProps.code,
                debug = false,
                systemWide = systemWide,
                jvmProps = defaultJvmProps.plus(
                    listOf(
                        "-DWT_APP_PATH=wt4",
                        "-DWT_APP_FLAVOR=${versionProps.flavor}",
                        "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
                        "--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED",
                        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
                    )
                ),
                sentryDsn = sentryProps.dsn,
                gaKey = deployProps.getProperty("ga"),
                j17HomeOverride = j17HomeOverride,
                jmodsHomeOverride = jmodsHomeOverride,
            )
        }

        fun asBasicMac(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            return asBasic(appType, project, systemWide = true)
        }

        /**
         * Legacy architecture support
         */
        fun asBasicMacX64(
            appType: AppType,
            project: Project,
            j17HomeOverride: String?,
            jmodsHomeOverride: String?,
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            if (j17HomeOverride == null || jmodsHomeOverride == null) {
                throw IllegalArgumentException("Cannot create legacy bundle with unprovided special j17 build")
            }
            return asBasic(
                appType,
                project,
                systemWide = true,
                j17HomeOverride = j17HomeOverride,
                jmodsHomeOverride = jmodsHomeOverride,
            )
        }

        fun asBasicLinux(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.LINUX) {
                throw IllegalArgumentException("Bundle designed for LINUX *ONLY*")
            }
            return asBasic(appType, project, systemWide = false)
        }

        fun asBasicWin(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            return asBasic(appType, project, systemWide = false)
        }
    }
}