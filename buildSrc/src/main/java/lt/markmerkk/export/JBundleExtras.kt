package lt.markmerkk.export

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalStateException
import java.util.*

class JBundleExtras(
        val debug: Boolean,
        val gaKey: String,
        val oauth: Boolean,
        val oauthKeyConsumer: String,
        val oauthKeyPrivate: String,
        val oauthHost: String
) {

    companion object {
        // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
        fun asDebugOauth(project: Project): JBundleExtras {
            val keysPropertyFile = File("${project.rootDir}/keys_debug", "private.properties")
            assert(keysPropertyFile.exists()) {
                throw IllegalStateException("Missing properties file")
            }
            val keysProperties = Properties().apply {
                load(FileInputStream(keysPropertyFile.absolutePath))
            }
            return JBundleExtras(
                    debug = true,
                    gaKey = "test",
                    oauth = true,
                    oauthKeyConsumer = keysProperties.getProperty("key_consumer"),
                    oauthKeyPrivate = keysProperties.getProperty("key_private"),
                    oauthHost = keysProperties.getProperty("host")
            )
        }

        fun asDebugBasic(project: Project): JBundleExtras {
            return JBundleExtras(
                    debug = true,
                    gaKey = "test",
                    oauth = false,
                    oauthKeyConsumer = "",
                    oauthKeyPrivate = "",
                    oauthHost = ""
            )
        }
    }
}