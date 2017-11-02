package lt.markmerkk.mvp

import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit

class AuthServiceImpl(
        private val view: AuthService.View,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler,
        private val authInteractor: AuthService.AuthInteractor,
        private val logLoader: LogLoader
) : AuthService {

    private val debugLogFilename = "info_prod.log"
    private var testConnectionSubscription: Subscription? = null
    private var fileLoadSubscription: Subscription? = null
    private var logDisplayType = AuthService.LogDisplayType.VISUAL

    override fun onAttach() {}

    override fun onDetach() {
        fileLoadSubscription?.unsubscribe()
        testConnectionSubscription?.unsubscribe()
    }

    override fun testLogin(
            hostname: String,
            username: String,
            password: String
    ) {
        testConnectionSubscription?.unsubscribe()
        testConnectionSubscription = Observable.defer({ authInteractor.jiraTestValidConnection(hostname, username, password) })
                .flatMap {
                    logger.info("[INFO] Success logging in!")
                    Observable.just(AuthService.AuthResult.SUCCESS)
                }
                .onErrorResumeNext({ Observable.just(handleError(it)) })
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnTerminate { view.hideProgress() }
                .subscribe({
                    view.showAuthResult(it)
                    loadOutputLogs()
                }, {
                    logger.error("[ERROR] Error checking status of jira validation")
                    loadOutputLogs()
                })
    }

    override fun logDisplayType(): AuthService.LogDisplayType = logDisplayType

    override fun toggleDisplayType() {
        when (logDisplayType) {
            AuthService.LogDisplayType.VISUAL -> {
                logDisplayType = AuthService.LogDisplayType.TEXT
                view.showDebugLogs()
                loadOutputLogs()
            }
            AuthService.LogDisplayType.TEXT -> {
                logDisplayType = AuthService.LogDisplayType.VISUAL
                view.hideDebugLogs()
            }
            else -> throw IllegalStateException("Unidentified display type")
        }
    }

    fun loadOutputLogs() {
        fileLoadSubscription?.unsubscribe()
        fileLoadSubscription = Observable.defer({ Observable.just(logLoader.loadLastLogs(debugLogFilename, 300)) })
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnError({ view.errorFillingDebugLogs(it) })
                .doOnNext({ view.fillDebugLogs(it) })
                .delay(50, TimeUnit.MILLISECONDS)
                .doOnNext({ view.scrollToBottomOfDebugLogs(it.length) })
                .subscribe()
    }

    fun handleError(error: Throwable): AuthService.AuthResult {
        if (error is IllegalArgumentException) {
            logger.warn("[WARNING] ${error.message}")
            return AuthService.AuthResult.ERROR_EMPTY_FIELDS
        }
        if (error.message != null && error.message!!.contains("401 Unauthorized")) {
            logger.warn("[WARNING] Unauthorized!")
            return AuthService.AuthResult.ERROR_UNAUTHORISED
        }
        if (error.message != null && error.message!!.contains("404 Not Found")) {
            logger.warn("[WARNING] Hostname not found!")
            return AuthService.AuthResult.ERROR_INVALID_HOSTNAME
        }
        logger.warn("[WARNING] Undefined error!", error)
        return AuthService.AuthResult.ERROR_UNDEFINED
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)!!
    }

}