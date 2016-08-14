package lt.markmerkk.ui.version

import com.vinumeris.updatefx.*
import lt.markmerkk.Config
import lt.markmerkk.Main
import org.bouncycastle.math.ec.ECPoint
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Path

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
class UpdaterImpl(
        config: Config
) : Updater(
        URI.create("https://dl.dropboxusercontent.com/u/60630588/updates/index/"),
        config.versionCode.toString(),
        AppDirectory.dir(),
        UpdateFX.findCodePath(Main::class.java),
        Crypto.decode("03277844CEBC197A402B292133CD20C34C8920F68CE33B93B7FA1779AE01E98D57"),
        1
) {
    override fun updateProgress(workDone: Long, max: Long) {
        super.updateProgress(workDone, max)
        logger.debug("UpdateProgress: $workDone / $max")
    }

    override fun updateValue(value: UpdateSummary?) {
        super.updateValue(value)
        logger.debug("UpdateValue: $value")
    }

    override fun succeeded() {
        super.succeeded()
        logger.debug("succeeded")
    }

    override fun failed() {
        super.failed()
        logger.debug("failed")
    }

    companion object {
        val logger = LoggerFactory.getLogger(UpdaterImpl::class.java)!!
    }

}