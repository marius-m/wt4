package lt.markmerkk.utils

import com.vinumeris.updatefx.*
import lt.markmerkk.Config
import lt.markmerkk.Main
import lt.markmerkk.entities.VersionSummary
import lt.markmerkk.entities.VersionSummaryImpl
import lt.markmerkk.interactors.VersionUpdater
import org.bouncycastle.math.ec.ECPoint
import org.slf4j.LoggerFactory
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import java.net.URI
import java.nio.file.Path

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
class VersionUpdaterImpl(
        config: Config,
        appDirectory: Path
) : VersionUpdater<UpdateSummary>, Updater(
        URI.create("https://dl.dropboxusercontent.com/u/60630588/updates/index/"), // todo eap index
        config.versionCode.toString(),
        appDirectory,
        UpdateFX.findCodePath(Main::class.java),
        Crypto.decode("03277844CEBC197A402B292133CD20C34C8920F68CE33B93B7FA1779AE01E98D57"),
        1
) {
    override var value: VersionSummary<UpdateSummary>? = null
        get() {
            if (getValue() == null)
                return null
            return VersionSummaryImpl(getValue())
        }

    override val progressSubject: BehaviorSubject<Float> = BehaviorSubject.create<Float>()!!

    override fun updateProgress(workDone: Long, max: Long) {
        super.updateProgress(workDone, max)
        val progress = (workDone * 100 / max).toFloat()
        val finalProgress = progress.toFloat() / 100
        progressSubject.onNext(finalProgress)
    }

    companion object {
        val logger = LoggerFactory.getLogger(VersionUpdaterImpl::class.java)!!
    }

}