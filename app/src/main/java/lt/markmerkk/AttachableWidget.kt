package lt.markmerkk

import org.slf4j.LoggerFactory

interface AttachableWidget {
    fun onDock()
    fun onUndock()
}

class LoggingWidget: AttachableWidget {
    override fun onDock() {
        l.debug("onDock(this: ${this.javaClass})")
    }

    override fun onUndock() {
        l.debug("onDock(this: ${this.javaClass})")
    }

    companion object {
        private val l = LoggerFactory.getLogger("LoggingWidget")!!
    }
}