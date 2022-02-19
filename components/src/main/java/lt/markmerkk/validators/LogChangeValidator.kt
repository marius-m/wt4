package lt.markmerkk.validators

import lt.markmerkk.WorklogStorage

/**
 * Defines rules for log changes
 */
class LogChangeValidator(
    private val worklogStorage: WorklogStorage
) {

    /**
     * @return true whenever log is valid for editing
     */
    fun canEditSimpleLog(logLocalId: Long): Boolean {
        return worklogStorage.findById(logLocalId) != null
    }
}