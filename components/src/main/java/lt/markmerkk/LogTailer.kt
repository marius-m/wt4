package lt.markmerkk

import org.apache.commons.io.input.Tailer
import org.apache.commons.io.input.TailerListener
import org.slf4j.LoggerFactory
import rx.*
import rx.functions.Action1
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * Tails log file
 * Lifecycle: [onAttach], [onDetach]
 */
class LogTailer(
        private val listener: Listener,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subscription: Subscription? = null

    fun onAttach() { }

    fun onDetach() {
        subscription?.unsubscribe()
    }

    fun tail(file: File) {
        subscription?.unsubscribe()
        val tailerEmitter = LogEmitter(file)
        subscription = Observable.create(tailerEmitter, Emitter.BackpressureMode.BUFFER)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnTerminate {
                    tailerEmitter.onTerminate()
                    logger.debug("Terminating tailer")
                }
                .subscribe({
                    listener.onLogUpdate(it)
                }, {
                    val stacktraceAsString = it.stackTrace.toList()
                            .map { stElement -> stElement.toString() }
                    listener.onLogUpdate("Error reading log: \n$stacktraceAsString")
                    logger.warn("Logger subscription error", it)
                })
    }

    fun clear() {
        subscription?.unsubscribe()
        listener.onClearLog()
    }

    private class LogEmitter(
            private val file: File
    ): Action1<Emitter<String>> {

        private var tailer: Tailer? = null

        override fun call(emitter: Emitter<String>) {

            val listener = object : TailerListener {

                override fun handle(line: String?) {
                    logger.debug("Emitting items: $line")
                    if (line != null) {
                        emitter.onNext(line)
                    }
                }

                override fun handle(ex: Exception) {
                    logger.debug("handle(): $ex")
                    tailer?.stop()
                    emitter.onError(ex)
                }

                override fun fileRotated() {
                    logger.debug("File rotated!")
                }

                override fun init(newTailer: Tailer) {
                    tailer = newTailer
                }

                override fun fileNotFound() {
                    tailer?.stop()
                    logger.warn("File not found!")
                    emitter.onError(IllegalArgumentException("File not found!"))
                }
            }
            Tailer.create(file, listener, 2000L)
        }

        fun onTerminate() {
            tailer?.stop()
        }

    }

    interface Listener {
        fun onLogUpdate(logAsString: String)
        fun onClearLog()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LogTailer::class.java)!!
    }

}