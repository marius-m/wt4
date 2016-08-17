package lt.markmerkk.utils.tracker

import com.brsanthu.googleanalytics.AppViewHit
import com.brsanthu.googleanalytics.EventHit
import com.brsanthu.googleanalytics.GoogleAnalytics
import lt.markmerkk.Config
import lt.markmerkk.Main

/**
 * Created by mariusmerkevicius on 3/11/16.
 * Represents tracker for tracking various events through [GoogleAnalytics]
 */
class GATracker(
        private val config: Config
) : ITracker {
    private val analytics: GoogleAnalytics

    init {
        analytics = GoogleAnalytics(config.gaKey, Main.APP_NAME, config.versionName)
    }

    override fun sendEvent(category: String, action: String, label: String, value: Int) {
        if (category.isNullOrEmpty()) return
        if (action.isNullOrEmpty()) return
        if (label.isNullOrEmpty()) return
        analytics.postAsync(EventHit(category, action, label, value))
    }

    override fun sendEvent(category: String, action: String) {
        if (category.isNullOrEmpty()) return
        if (action.isNullOrEmpty()) return
        analytics.postAsync(EventHit(category, action))
    }

    override fun sendView(contentDescription: String) {
        if (contentDescription.isNullOrEmpty()) return
        analytics.postAsync(AppViewHit(Main.APP_NAME, config.versionName, contentDescription))
    }

    override fun stop() {
        analytics.close()
    }
}
