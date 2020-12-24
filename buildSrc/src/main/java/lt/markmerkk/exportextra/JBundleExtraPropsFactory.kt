package lt.markmerkk.exportextra

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*
import lt.markmerkk.export.tasks.OsType
import java.lang.IllegalArgumentException

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
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, APP_FLAVOR_BASIC)
            return JBundleExtraProps(
                    appName = "${APP_NAME}-$versionNameSuffix",
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
                    gaKey = "test",
                    oauth = false,
                    oauthKeyConsumer = "",
                    oauthKeyPrivate = "",
                    oauthHost = ""
            )
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        fun asOauthITO(
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_ITO)
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys_debug", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                    appName = "${APP_NAME}-$versionNameSuffix",
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
                versionNameSuffix: String,
                project: Project,
                systemWide: Boolean
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_BASIC)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                    appName = "${APP_NAME}-$versionNameSuffix",
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
                    gaKey = deployProps.getProperty("ga"),
                    oauth = false,
                    oauthKeyConsumer = "",
                    oauthKeyPrivate = "",
                    oauthHost = ""
            )
        }

        fun asBasicMac(
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            return asBasic(versionNameSuffix, project, systemWide = true)
        }

        fun asBasicWin(
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            return asBasic(versionNameSuffix, project, systemWide = false)
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        private fun asOauthITO(
                versionNameSuffix: String,
                project: Project,
                systemWide: Boolean
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_ITO)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                    appName = "${APP_NAME}-$versionNameSuffix",
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
                    gaKey = deployProps.getProperty("ga"),
                    oauth = true,
                    oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                    oauthKeyPrivate = keysProperties.getProperty("key_private"),
                    oauthHost = keysProperties.getProperty("host")
            )
        }

        fun asOauthITOMac(
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            return asOauthITO(versionNameSuffix, project, systemWide = true)
        }

        fun asOauthITOWin(
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            return asOauthITO(versionNameSuffix, project, systemWide = false)
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        fun asOauthITOCustomSystemWideWindows(
                versionNameSuffix: String,
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            val versionProps = VersionProps.fromProps(project, flavor = APP_FLAVOR_ITO)
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
                    appName = "${APP_NAME}-$versionNameSuffix",
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
                    gaKey = deployProps.getProperty("ga"),
                    oauth = true,
                    oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                    oauthKeyPrivate = keysProperties.getProperty("key_private"),
                    oauthHost = keysProperties.getProperty("host")
            )
        }
    }

}