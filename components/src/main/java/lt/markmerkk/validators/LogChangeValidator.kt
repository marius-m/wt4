package lt.markmerkk.validators

import lt.markmerkk.LogStorage

/**
 * Defines rules for log changes
 */
class LogChangeValidator(
        private val logStorage: LogStorage
) {

    /**
     * @return true whenever log is valid for editing
     */
    fun canEditSimpleLog(simpleLogId: Long): Boolean {
        val simpleLog = logStorage.findByIdOrNull(simpleLogId) ?: return false
        return !simpleLog.isRemote // cannot edit remote id
    }
}