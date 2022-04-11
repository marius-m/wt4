package lt.markmerkk.exportextra

import lt.markmerkk.export.tasks.OsType
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*

object JBundleExtraPropsFactory {

    const val APP_NAME = "WT4"
    const val APP_FLAVOR_BASIC = "basic"
    const val APP_FLAVOR_ITO = "ito"

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
                oauth = false,
                oauthKeyConsumer = "",
                oauthKeyPrivate = "",
                oauthHost = ""
            )
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        fun asOauthITO(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_ITO)
            val sentryProps = SentryProps.fromProps(project)
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys_debug", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
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
                oauth = true,
                oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                oauthKeyPrivate = keysProperties.getProperty("key_private"),
                oauthHost = keysProperties.getProperty("host")
            )
        }
    }

    object Release {

        private fun asBasic(
            appType: AppType,
            project: Project,
            systemWide: Boolean
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
                        "-DWT_APP_FLAVOR=${versionProps.flavor}"
                    )
                ),
                sentryDsn = sentryProps.dsn,
                gaKey = deployProps.getProperty("ga"),
                oauth = false,
                oauthKeyConsumer = "",
                oauthKeyPrivate = "",
                oauthHost = ""
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

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        private fun asOauthITO(
            appType: AppType,
            project: Project,
            systemWide: Boolean
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_ITO)
            val sentryProps = SentryProps.fromProps(project)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
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
                        "-DWT_APP_FLAVOR=${versionProps.flavor}"
                    )
                ),
                sentryDsn = sentryProps.dsn,
                gaKey = deployProps.getProperty("ga"),
                oauth = true,
                oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                oauthKeyPrivate = keysProperties.getProperty("key_private"),
                oauthHost = keysProperties.getProperty("host")
            )
        }

        fun asOauthITOMac(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            return asOauthITO(appType, project, systemWide = true)
        }

        fun asOauthITOLinux(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.LINUX) {
                throw IllegalArgumentException("Bundle designed for LINUX *ONLY*")
            }
            return asOauthITO(appType, project, systemWide = false)
        }

        fun asOauthITOWin(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            return asOauthITO(appType, project, systemWide = false)
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        fun asOauthITOCustomSystemWideWindows(
            appType: AppType,
            project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_ITO)
            val sentryProps = SentryProps.fromProps(project)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
            val rootDirPath = "C:${File.separator}WT4"
            val appDirPath = "${rootDirPath}${File.separator}.wt4"
            val tmpDirPath = "${appDirPath}${File.separator}tmp"
            return JBundleExtraProps(
                appName = "${APP_NAME}-${appType.rawType}",
                appType = appType,
                appFlavor = versionProps.flavor,
                versionName = versionProps.name,
                versionCode = versionProps.code,
                debug = false,
                systemWide = true,
                jvmProps = defaultJvmProps.plus(
                    listOf(
                        "-DWT_APP_PATH=wt4",
                        "-DWT_ROOT=$rootDirPath",
                        "-Djava.io.tmpdir=$tmpDirPath",
                        "-DWT_APP_FLAVOR=${versionProps.flavor}"
                    )
                ),
                sentryDsn = sentryProps.dsn,
                gaKey = deployProps.getProperty("ga"),
                oauth = true,
                oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                oauthKeyPrivate = keysProperties.getProperty("key_private"),
                oauthHost = keysProperties.getProperty("host")
            )
        }
    }
}