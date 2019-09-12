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

        // test data
        const val URL = "http://localhost:2990/jira"
        const val CONSUMER_KEY = "dpf43f3p2l4k3l03"
        const val PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALRiMLAh9iimur8VA7qVvdqxevEuUkW4K+2KdMXmnQbG9Aa7k7eBjK1S+0LYmVjPKlJGNXHDGuy5Fw/d7rjVJ0BLB+ubPK8iA/Tw3hLQgXMRRGRXXCn8ikfuQfjUS1uZSatdLB81mydBETlJhI6GH4twrbDJCR2Bwy/XWXgqgGRzAgMBAAECgYBYWVtleUzavkbrPjy0T5FMou8HX9u2AC2ry8vD/l7cqedtwMPp9k7TubgNFo+NGvKsl2ynyprOZR1xjQ7WgrgVB+mmuScOM/5HVceFuGRDhYTCObE+y1kxRloNYXnx3ei1zbeYLPCHdhxRYW7T0qcynNmwrn05/KO2RLjgQNalsQJBANeA3Q4Nugqy4QBUCEC09SqylT2K9FrrItqL2QKc9v0ZzO2uwllCbg0dwpVuYPYXYvikNHHg+aCWF+VXsb9rpPsCQQDWR9TT4ORdzoj+NccnqkMsDmzt0EfNaAOwHOmVJ2RVBspPcxt5iN4HI7HNeG6U5YsFBb+/GZbgfBT3kpNGWPTpAkBI+gFhjfJvRw38n3g/+UeAkwMI2TJQS4n8+hid0uus3/zOjDySH3XHCUnocn1xOJAyZODBo47E+67R4jV1/gzbAkEAklJaspRPXP877NssM5nAZMU0/O/NGCZ+3jPgDUno6WbJn5cqm8MqWhW1xGkImgRk+fkDBquiq4gPiT898jusgQJAd5Zrr6Q8AO/0isr/3aa6O6NLQxISLKcPDk2NOccAfS/xOtfOz4sJYM3+Bs4Io9+dZGSDCA54Lw03eHTNQghS0A\\=\\="
    }

}