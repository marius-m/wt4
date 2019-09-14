package lt.markmerkk.widgets.settings

import lt.markmerkk.Tags
import lt.markmerkk.UserSettings
import net.rcarz.jiraclient.JiraApi
import org.scribe.builder.ServiceBuilder
import org.scribe.model.SignatureType
import org.scribe.model.Token
import org.scribe.model.Verifier
import org.scribe.oauth.OAuthService
import org.slf4j.LoggerFactory
import rx.Single

// todo incomplete behaviour
class OAuthInteractor(
        private val userSettings: UserSettings
) {

    private lateinit var jiraApi: JiraApi
    private lateinit var service: OAuthService
    private lateinit var requestToken: Token

    /**
     * Generates auth url to begin auth
     */
    fun generateAuthUrl(): Single<String> {
        return Single.defer {
            logger.debug("Generating new token for authorization")
            val oauthPreset = userSettings.jiraOAuthPreset()
            jiraApi = JiraApi(oauthPreset.host, oauthPreset.privateKey)
            service = ServiceBuilder()
                    .provider(jiraApi)
                    .apiKey(oauthPreset.consumerKey)
                    .apiSecret(oauthPreset.privateKey)
                    .signatureType(SignatureType.Header)
                    .build()
            requestToken = service.requestToken
            logger.debug("Creating new authorization URL")
            Single.just(service.getAuthorizationUrl(requestToken))
        }
    }

    fun generateToken(accessTokenKey: String): Single<OAuthToken> {
        return Single.defer {
            val accessToken = service.getAccessToken(requestToken, Verifier(accessTokenKey))
            Single.just(OAuthToken(requestToken.token, accessToken.token))
        }
    }

    data class OAuthToken(val tokenSecret: String, val accessKey: String)

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}