package lt.markmerkk.exportextra

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*

object JBundleExtraPropsFactory {

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
                    gaKey = "test",
                    oauth = false,
                    oauthKeyConsumer = "",
                    oauthKeyPrivate = "",
                    oauthHost = ""
            )
        }

        fun asBasicSystemWide(
                project: Project
        ): JBundleExtraProps {
            val versionProps = VersionProps.fromProps(project)
            return JBundleExtraProps(
                    versionName = versionProps.name,
                    versionCode = versionProps.code,
                    debug = true,
                    systemWide = true,
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
                    gaKey = "test",
                    oauth = true,
                    oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                    oauthKeyPrivate = keysProperties.getProperty("key_private"),
                    oauthHost = keysProperties.getProperty("host")
            )
        }
    }

    object Release {

        fun asBasic(
                project: Project
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
                    systemWide = false,
                    gaKey = deployProps.getProperty("ga"),
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
                    systemWide = false,
                    gaKey = deployProps.getProperty("ga"),
                    oauth = true,
                    oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                    oauthKeyPrivate = keysProperties.getProperty("key_private"),
                    oauthHost = keysProperties.getProperty("host")
            )
        }
    }

}