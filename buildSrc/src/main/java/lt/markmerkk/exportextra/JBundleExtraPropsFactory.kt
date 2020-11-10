package lt.markmerkk.exportextra

import com.google.wireless.android.play.playlog.proto.ClientAnalytics
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*
import lt.markmerkk.export.tasks.OsType
import java.lang.IllegalArgumentException

object JBundleExtraPropsFactory {

    private val defaultJvmProps = listOf(
            "-Xms128m",
            "-Xmx300m",
            "-XX:+UseG1GC",
            "-splash:\$APPDIR/resources/splash.png"
    )

    object Debug {
        fun asBasic(
                project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project)
            return JBundleExtraProps(
                    versionName = versionProps.name,
                    versionCode = versionProps.code,
                    debug = true,
                    systemWide = false,
                    jvmProps = defaultJvmProps.plus(
                            listOf("-DWT_APP_PATH=wt4_debug")
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
                project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project)
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys_debug", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                    versionName = versionProps.name,
                    versionCode = versionProps.code,
                    debug = true,
                    systemWide = false,
                    jvmProps = defaultJvmProps.plus(
                            listOf("-DWT_APP_PATH=wt4_debug")
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
                project: Project,
                systemWide: Boolean
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                    versionName = versionProps.name,
                    versionCode = versionProps.code,
                    debug = false,
                    systemWide = systemWide,
                    jvmProps = defaultJvmProps.plus(
                            listOf("-DWT_APP_PATH=wt4")
                    ),
                    gaKey = deployProps.getProperty("ga"),
                    oauth = false,
                    oauthKeyConsumer = "",
                    oauthKeyPrivate = "",
                    oauthHost = ""
            )
        }

        fun asBasicMac(
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            return asBasic(project, systemWide = true)
        }

        fun asBasicWin(
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            return asBasic(project, systemWide = false)
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        private fun asOauthITO(
                project: Project,
                systemWide: Boolean
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project)
            val deployProps = Properties().apply {
                val deployPropertyFile = File("${project.rootDir}", "deploy.properties")
                load(FileInputStream(deployPropertyFile.absolutePath))
            }
            val keysProperties = Properties().apply {
                val keysPropertyFile = File("${project.rootDir}/keys", "private.properties")
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
            return JBundleExtraProps(
                    versionName = versionProps.name,
                    versionCode = versionProps.code,
                    debug = false,
                    systemWide = systemWide,
                    jvmProps = defaultJvmProps.plus(
                            listOf("-DWT_APP_PATH=wt4")
                    ),
                    gaKey = deployProps.getProperty("ga"),
                    oauth = true,
                    oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                    oauthKeyPrivate = keysProperties.getProperty("key_private"),
                    oauthHost = keysProperties.getProperty("host")
            )
        }

        fun asOauthITOMac(
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.MAC) {
                throw IllegalArgumentException("Bundle designed for MACOSX *ONLY*")
            }
            return asOauthITO(project, systemWide = true)
        }

        fun asOauthITOWin(
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            return asOauthITO(project, systemWide = false)
        }

        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        fun asOauthITOCustomSystemWideWindows(
                project: Project
        ): JBundleExtraProps {
            if (OsType.get() != OsType.WINDOWS) {
                throw IllegalArgumentException("Bundle designed for Windows *ONLY*")
            }
            val versionProps = VersionProps.fromProps(project)
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
                    versionName = versionProps.name,
                    versionCode = versionProps.code,
                    debug = false,
                    systemWide = true,
                    jvmProps = defaultJvmProps.plus(
                            listOf(
                                    "-DWT_APP_PATH=wt4",
                                    "-DWT_ROOT=$rootDirPath",
                                    "-Djava.io.tmpdir=$tmpDirPath"
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